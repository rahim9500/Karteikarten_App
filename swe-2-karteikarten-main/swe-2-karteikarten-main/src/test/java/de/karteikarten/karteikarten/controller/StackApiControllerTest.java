package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.models.Stack;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import de.karteikarten.karteikarten.repository.StackRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StackApiController.class)
public class StackApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlashcardRepository flashcardRepository;

    @MockBean
    private StackRepository stackRepository;

    @Test
    public void getFlashcardsByStackIdReturnsNotEmptyList() throws Exception {
        List<Flashcard> flashcards = Arrays.asList(new Flashcard());

        try (MockedStatic<FlashcardRepository> mockedFlashcardRepo = mockStatic(FlashcardRepository.class)) {
            mockedFlashcardRepo.when(() -> FlashcardRepository.findFlashcardsByStackId(1)).thenReturn(flashcards);

            mockMvc.perform(get("/api/stack/1/flashcards"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").exists());
        }
    }

    @Test
    public void getFlashcardsByStackIdReturnsEmptyList() throws Exception {
        try (MockedStatic<FlashcardRepository> mockedFlashcardRepo = mockStatic(FlashcardRepository.class)) {
            mockedFlashcardRepo.when(() -> FlashcardRepository.findFlashcardsByStackId(1)).thenReturn(new ArrayList<>());

            mockMvc.perform(get("/api/stack/1/flashcards"))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    public void updateStackWhenUserNotLoggedIn() throws Exception {
        mockMvc.perform(post("/api/updateStack/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flashcardIds\":[\"1\",\"2\"]}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Benutzer ist nicht eingeloggt."));
    }

    @Test
    public void updateStackSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);

        try (MockedStatic<StackRepository> mockedStackRepo = mockStatic(StackRepository.class)) {
            Stack mockStack = new Stack();
            mockedStackRepo.when(() -> StackRepository.findStackById(1)).thenReturn(mockStack);
            mockedStackRepo.when(() -> StackRepository.updateStack(anyInt(), anyList())).thenReturn(true);

            mockMvc.perform(post("/api/updateStack/1")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"flashcardIds\":[\"1\",\"2\"]}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Stack erfolgreich aktualisiert."));
        }
    }

    @Test
    public void updateStackFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);

        try (MockedStatic<StackRepository> mockedStackRepo = mockStatic(StackRepository.class)) {
            Stack mockStack = new Stack();
            mockedStackRepo.when(() -> StackRepository.findStackById(1)).thenReturn(mockStack);
            mockedStackRepo.when(() -> StackRepository.updateStack(anyInt(), anyList())).thenReturn(false);

            mockMvc.perform(post("/api/updateStack/1")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"flashcardIds\":[\"1\",\"2\"]}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Stack konnte nicht aktualisiert werden."));
        }
    }
}