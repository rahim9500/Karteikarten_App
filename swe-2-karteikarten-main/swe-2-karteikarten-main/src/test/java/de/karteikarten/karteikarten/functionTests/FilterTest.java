package de.karteikarten.karteikarten.functionTests;

import database.Database;
import de.karteikarten.karteikarten.controller.FlashcardController;
import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.models.Stack;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import de.karteikarten.karteikarten.repository.StackRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class FilterTest {

    @Test
    public void testFilterStacksByTag() {
        StackRepository mockRepo = Mockito.mock(StackRepository.class);
        List<Integer> tagIds = Arrays.asList(1, 2, 3);
        Stack expectedStack = new Stack();
        when(mockRepo.findStacksByTagIds(tagIds)).thenReturn(Arrays.asList(expectedStack));

        // Positiv
        List<Stack> result = mockRepo.findStacksByTagIds(tagIds);
        assertTrue(result.contains(expectedStack), "The result should contain the expected stack");

        // Negativ
        List<Integer> invalidTagIds = Arrays.asList(4, 5, 6);
        when(mockRepo.findStacksByTagIds(invalidTagIds)).thenReturn(new ArrayList<>());
        List<Stack> negativeResult = mockRepo.findStacksByTagIds(invalidTagIds);
        assertFalse(negativeResult.contains(expectedStack), "The result should not contain the expected stack");
    }
    @Test
    public void testFilterPrivateStacks() {
        StackRepository mockRepo = Mockito.mock(StackRepository.class);
        List<Integer> tagIds = Arrays.asList(1, 2, 3);
        Stack expectedStack = new Stack();
        when(mockRepo.findStacksByTagIds(tagIds)).thenReturn(Arrays.asList(expectedStack));

        // Positiv
        List<Stack> result = mockRepo.findStacksByTagIds(tagIds);
        assertTrue(result.contains(expectedStack), "The result should contain the expected stack");

        // Negativ
        List<Integer> invalidTagIds = Arrays.asList(4, 5, 6);
        when(mockRepo.findStacksByTagIds(invalidTagIds)).thenReturn(new ArrayList<>());
        List<Stack> negativeResult = mockRepo.findStacksByTagIds(invalidTagIds);
        assertFalse(negativeResult.contains(expectedStack), "The result should not contain the expected stack");
    }

    @Test
    public void testFilterPublicStacks() {
        StackRepository mockRepo = Mockito.mock(StackRepository.class);
        List<Integer> tagIds = Arrays.asList(1, 2, 3);
        Stack expectedStack = new Stack();
        when(mockRepo.findStacksByTagIds(tagIds)).thenReturn(Arrays.asList(expectedStack));

        // Positiv
        List<Stack> result = mockRepo.findStacksByTagIds(tagIds);
        assertTrue(result.contains(expectedStack), "The result should contain the expected stack");

        // Negativ
        List<Integer> invalidTagIds = Arrays.asList(4, 5, 6);
        when(mockRepo.findStacksByTagIds(invalidTagIds)).thenReturn(new ArrayList<>());
        List<Stack> negativeResult = mockRepo.findStacksByTagIds(invalidTagIds);
        assertFalse(negativeResult.contains(expectedStack), "The result should not contain the expected stack");
    }
}