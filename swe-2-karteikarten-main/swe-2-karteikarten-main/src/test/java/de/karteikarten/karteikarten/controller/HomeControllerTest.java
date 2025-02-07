package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.models.Stack;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import de.karteikarten.karteikarten.repository.StackRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StackRepository stackRepository;

    @MockBean
    private FlashcardRepository flashcardRepository;

    @Test
    public void testStartseiteWithLoggedInUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);
        List<Flashcard> flashcards = Arrays.asList(new Flashcard());
        List<Stack> stacks = Arrays.asList(new Stack());

        given(flashcardRepository.getAllFlashcardbyUser(1)).willReturn(flashcards);
        given(stackRepository.findAllStacks(1)).willReturn(stacks);

        mockMvc.perform(get("/startseite").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("flashcards"))
                .andExpect(model().attributeExists("stacks"))
                .andExpect(view().name("startseite"));
    }

    @Test
    public void testStartseiteWithoutLoggedInUser() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/startseite").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("startseite"));
    }

    @Test
    public void testDeleteSelectedItems() throws Exception {
        mockMvc.perform(post("/deleteSelectedItems")
                        .param("selectedStacks", "1,2,3")
                        .param("selectedFlashcards", "4,5,6"))
                .andExpect(status().isOk())
                .andExpect(content().string("Selected items deleted successfully."));

        verify(stackRepository).deleteAllByIdIn(Arrays.asList(1, 2, 3));
        verify(flashcardRepository).deleteAllByIdIn(Arrays.asList(4, 5, 6));
    }
}