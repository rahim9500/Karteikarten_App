package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Stack;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import de.karteikarten.karteikarten.repository.StackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StackControllerTest {

    @InjectMocks
    private StackController stackController;

    @Mock
    private FlashcardRepository flashcardRepository;

    @Mock
    private StackRepository stackRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stackController).build();
    }
    @Test
    void createStack_WhenUserNotLoggedIn_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/createStack")
                        .param("title", "Test Stack")
                        .param("flashcardIdsString", "1,2,3")
                        .param("isPrivate", "false"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createStack_WhenUserLoggedInAndStackCreationSucceeds_ReturnsOk() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);

        when(stackRepository.createStack(any(Stack.class), anyList(), anyInt(), anyString())).thenReturn(1);

        mockMvc.perform(post("/createStack")
                        .param("title", "Test Stack")
                        .param("flashcardIdsString", "1,2,3")
                        .param("isPrivate", "false")
                        .session(session))
                .andExpect(status().isOk());
    }
    @Test
    void searchPrivateStacks_WhenUserNotLoggedIn_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/searchPrivateStacks").param("query", "Test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchPrivateStacks_WhenUserLoggedInAndQueryIsValid_ReturnsOk() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);

        Mockito.when(stackRepository.searchStacks(anyString(), anyInt())).thenReturn(Collections.singletonList(new Stack()));

        mockMvc.perform(get("/searchPrivateStacks").param("query", "Test").session(session))
                .andExpect(status().isOk());
    }
}
