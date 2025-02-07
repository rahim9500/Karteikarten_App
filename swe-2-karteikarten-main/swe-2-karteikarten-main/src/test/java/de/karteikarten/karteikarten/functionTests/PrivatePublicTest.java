package de.karteikarten.karteikarten.functionTests;

import de.karteikarten.karteikarten.models.Stack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrivatePublicTest {


    @Test
    public void testPrivateStack() {
        List<String> flashcards = new ArrayList<>();
        Stack stack = new Stack("Test Stack", flashcards, true, "hans");
        assertTrue(stack.isPrivate(), "Expected stack to be private");
    }

    @Test
    public void testPublicStack() {
        List<String> flashcards = new ArrayList<>();
        Stack stack = new Stack("Test Stack", flashcards, false, "hans");
        assertFalse(stack.isPrivate(), "Expected stack to be public");
    }
}