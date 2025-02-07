package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class UserController {


    private final UserRepository repository = new UserRepository();
    private final JavaMailSender mailSender;

    @Autowired
    public UserController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }





    // Navigation


    @GetMapping("/forgetpassword")
    public String forgetpassword() {
        return "PasswortReset";
    }


    // Methods

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {


        if (!repository.userExists(username, email)) {
            UserRepository.insertUserData(username, email, password);
            redirectAttributes.addFlashAttribute("successMessage", "Registrierung war erfolgreich.");
            return"redirect:/showLogin";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Die angegebene E-Mail-Adresse: " + email + " oder der Username: " + username + " ist bereits registriert. Bitte wählen Sie eine andere E-Mail-Adresse oder einen anderen Usernamen.");
            return "redirect:/registration";
        }

    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        boolean isValidUser = UserRepository.checkLoginCredentials(username, password);
        if (isValidUser) {
            Integer userId = UserRepository.getUserId(username);
            session.setAttribute("loggedInUser", username);
            session.setAttribute("loggedInUserId", userId);
            redirectAttributes.addFlashAttribute("successMessage", "Login war erfolgreich.");
            return "redirect:/startseite";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Fehlerhafte Username/Passwort Kombination. Beachten Sie Groß- und Kleinschreibung.");
            return "redirect:/showLogin";
        }
    }





    @GetMapping("/logout")
    public String logout(HttpSession session,
                         RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Sie wurden erfolgreich abgemeldet.");
        return "redirect:/showLogin";
    }


    // Reset Password Methods

    private void sendPasswordResetEmail(String email, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("karteikartenswt@outlook.de");
        message.setTo(email);
        message.setSubject("Passwort zurücksetzen");
        message.setText("Um Ihr Passwort zurückzusetzen, klicken Sie bitte auf den folgenden Link:\n " + resetLink);
        mailSender.send(message);
    }

    @PostMapping("/forgetpasswordemail")
    public ModelAndView handleForgetPassword(@RequestParam String email,
                                             RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        if (repository.emailExists(email)) {
            String token = generateResetToken();
            repository.savePasswordResetToken(email, token);
            String resetLink = "localhost:8080/resetPasswordForm?token=" + token;
            sendPasswordResetEmail(email, resetLink);

            redirectAttributes.addFlashAttribute("successMessage", "Wir haben einen Link zum Zurücksetzen des Passworts gesendet.");
            modelAndView.setViewName("redirect:/forgetpassword");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Die angegebene E-Mail-Adresse: " + email + " ist in unserem System nicht registriert. Bitte stellen Sie sicher, dass Sie die richtige E-Mail-Adresse verwenden oder registrieren Sie sich, falls Sie noch kein Konto haben.");
            modelAndView.setViewName("redirect:/forgetpassword");
        }
        return modelAndView;
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        if (UserRepository.checkIfTokenExists(token)) {
            UserRepository.updatePasswordUsingToken(token, newPassword);
            return "redirect:/showLogin";
        } else {
            return "redirect:/errorPage";
        }
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
    @GetMapping("/resetPasswordForm")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "resetPasswordForm";
    }





}
