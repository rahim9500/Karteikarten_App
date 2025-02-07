package de.karteikarten.karteikarten.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(RedirectController.class)
public class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void redirectToHomeWhenUserLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);

        mockMvc.perform(get("/")
                        .session(session))
                .andExpect(redirectedUrl("/startseite?userId=1"));
    }

    @Test
    public void redirectToLoginWhenUserNotLoggedIn() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("/showLogin"));
    }
}