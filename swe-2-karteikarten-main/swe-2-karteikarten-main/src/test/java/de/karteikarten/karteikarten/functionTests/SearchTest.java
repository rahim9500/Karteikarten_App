package de.karteikarten.karteikarten.functionTests;

import de.karteikarten.karteikarten.models.Stack;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {

    @Test
    public void testSearchByNamePositive() {
        List<Stack> stacks = new ArrayList<>();
        stacks.add(new Stack("Java Basics", new ArrayList<>(), false, "Author Name"));
        stacks.add(new Stack("Advanced Java", new ArrayList<>(), false, "Author Name"));

        List<Stack> result = stacks.stream()
                .filter(stack -> stack.getTitle().contains("Java"))
                .collect(Collectors.toList());

        // Positiv
        assertEquals(2, result.size(), "Search should return two stacks that contain 'Java'");
    }

    @Test
    public void testSearchByNameNegative() {
        List<Stack> stacks = new ArrayList<>();
        stacks.add(new Stack("Java Basics", new ArrayList<>(), false, "Author Name"));
        stacks.add(new Stack("Advanced Java", new ArrayList<>(), false, "Author Name"));


        List<Stack> result = stacks.stream()
                .filter(stack -> stack.getTitle().contains("C++"))
                .collect(Collectors.toList());

        // Negativ
        assertTrue(result.isEmpty(), "Search should return no stacks because 'C++' is not in any stack title");
    }

    @Test
    public void testSearchByEmptyStringReturnsAll() {
        List<Stack> stacks = new ArrayList<>();
        stacks.add(new Stack("Python Introduction", new ArrayList<>(), true, "Author Name"));
        stacks.add(new Stack("C++ Introduction", new ArrayList<>(), false, "Author Name"));

        List<Stack> result = stacks.stream()
                .filter(stack -> stack.getTitle().contains(""))
                .collect(Collectors.toList());

        assertEquals(2, result.size(), "Search with empty string should return all stacks");
    }
}
