package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class FlashcardApiControllerTest {

    @InjectMocks
    private FlashcardApiController flashcardApiController;

    @Mock
    private FlashcardRepository flashcardRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFlashcards_WhenUserNotLoggedIn_ReturnsUnauthorized() {
        ResponseEntity<List<Flashcard>> response = flashcardApiController.getFlashcards(new MockHttpSession());
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void getFlashcards_WhenUserLoggedInAndHasFlashcards_ReturnsFlashcards() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);

        List<Flashcard> flashcards = Arrays.asList(new Flashcard(), new Flashcard());
        when(flashcardRepository.getAllFlashcardbyUser(anyInt())).thenReturn(flashcards);

        ResponseEntity<List<Flashcard>> response = flashcardApiController.getFlashcards(session);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getFlashcards_WhenUserLoggedInAndHasNoFlashcards_ReturnsEmptyList() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);

        when(flashcardRepository.getAllFlashcardbyUser(anyInt())).thenReturn(Collections.emptyList());

        ResponseEntity<List<Flashcard>> response = flashcardApiController.getFlashcards(session);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, response.getBody().size());
    }
}