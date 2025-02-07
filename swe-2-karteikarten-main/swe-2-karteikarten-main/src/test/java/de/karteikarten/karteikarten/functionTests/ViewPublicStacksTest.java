package de.karteikarten.karteikarten.functionTests;

import database.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.karteikarten.karteikarten.models.Stack;
import de.karteikarten.karteikarten.repository.StackRepository;
import de.karteikarten.karteikarten.controller.StackController;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ViewPublicStacksTest {

    @Mock
    StackController stackController;

    @Test
    public void userCanViewPublicStack() {
        // Positiv
        Stack publicStack1 = new Stack();
        Stack publicStack2 = new Stack();
        List<Stack> publicStacks = Arrays.asList(publicStack1, publicStack2);
        ResponseEntity<List<Stack>> mockResponseEntity = ResponseEntity.ok(publicStacks);
        when(stackController.getPublicStacks()).thenReturn(mockResponseEntity);
        ResponseEntity<List<Stack>> responseEntity = stackController.getPublicStacks();
        List<Stack> result = responseEntity.getBody();
        assertEquals(publicStacks, result);
    }

    @Test
    public void userCannotViewPublicStackWhenResponseEntityIsNull() {
        // Negativ
        when(stackController.getPublicStacks()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> {
            ResponseEntity<List<Stack>> responseEntity = stackController.getPublicStacks();
            List<Stack> result = responseEntity.getBody();
        });
    }
}
