package de.karteikarten.karteikarten.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RedirectController {

    @GetMapping
    public String redirectToHomeOrLogin(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId != null) {
            return "redirect:/startseite?userId=" + userId;
        } else {
            return "redirect:/showLogin";
        }
    }
    
}
