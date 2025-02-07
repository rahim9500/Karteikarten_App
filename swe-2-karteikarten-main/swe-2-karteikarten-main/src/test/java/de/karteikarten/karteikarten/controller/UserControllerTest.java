package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void testForgetPassword() throws Exception {
        mockMvc.perform(get("/forgetpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("PasswortReset"));
    }

    @Test
    public void testRegister() throws Exception {
        try (MockedStatic<UserRepository> mocked = Mockito.mockStatic(UserRepository.class)) {
            mocked.when(() -> UserRepository.userExists("username", "email")).thenReturn(false);

            mockMvc.perform(post("/register")
                            .param("username", "username")
                            .param("email", "email")
                            .param("password", "password"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/showLogin"));
        }
    }
    @Test
    public void testRegisterWithExistingUser() throws Exception {
        try (MockedStatic<UserRepository> mocked = Mockito.mockStatic(UserRepository.class)) {
            mocked.when(() -> UserRepository.userExists("username", "email")).thenReturn(true);

            mockMvc.perform(post("/register")
                            .param("username", "username")
                            .param("email", "email")
                            .param("password", "password"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/registration"));
        }
    }

    @Test
    public void testRegisterWithExistingUserErrorMessage() throws Exception {
        try (MockedStatic<UserRepository> mocked = Mockito.mockStatic(UserRepository.class)) {
            mocked.when(() -> UserRepository.userExists("username", "email")).thenReturn(true);

            mockMvc.perform(post("/register")
                            .param("username", "username")
                            .param("email", "email")
                            .param("password", "password"))
                    .andExpect(flash().attribute("errorMessage", "Die angegebene E-Mail-Adresse: email oder der Username: username ist bereits registriert. Bitte wählen Sie eine andere E-Mail-Adresse oder einen anderen Usernamen."));
        }
    }
    @Test
    public void testLogin() throws Exception {
        try (MockedStatic<UserRepository> mocked = Mockito.mockStatic(UserRepository.class)) {
            mocked.when(() -> UserRepository.checkLoginCredentials("username", "password")).thenReturn(true);

            MockHttpSession session = new MockHttpSession();
            mockMvc.perform(post("/login")
                            .param("username", "username")
                            .param("password", "password")
                            .session(session))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/startseite"));
        }
    }
    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        try (MockedStatic<UserRepository> mocked = Mockito.mockStatic(UserRepository.class)) {
            mocked.when(() -> UserRepository.checkLoginCredentials("username", "password")).thenReturn(false);

            MockHttpSession session = new MockHttpSession();
            mockMvc.perform(post("/login")
                            .param("username", "username")
                            .param("password", "password")
                            .session(session))
                    .andExpect(flash().attribute("errorMessage", "Fehlerhafte Username/Passwort Kombination. Beachten Sie Groß- und Kleinschreibung."))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/showLogin"));
        }
    }

}