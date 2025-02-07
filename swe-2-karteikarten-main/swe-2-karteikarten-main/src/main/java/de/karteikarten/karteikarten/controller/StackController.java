package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.models.Stack;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import de.karteikarten.karteikarten.repository.StackRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StackController {


    @Autowired
    private FlashcardRepository flashcardRepository;
    @Autowired
    private StackRepository stackRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @GetMapping("/createStackForm")
    public String showCreateStackForm() {
        return "StackErstellen";
    }

    @PostMapping("/createStack")
    public ResponseEntity<Map<String, Object>> createStack(@RequestParam("title") String title,
                                                           @RequestParam("flashcardIdsString") String flashcardIdsString,
                                                           @RequestParam("isPrivate") boolean isPrivate,
                                                           HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer userId = (Integer) session.getAttribute("loggedInUserId");
            String author = (String) session.getAttribute("author"); // Get the author from the session

            if (userId == null) {
                response.put("success", false);
                response.put("message", "Benutzer ist nicht eingeloggt.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<String> flashcardIds = Arrays.asList(flashcardIdsString.split(","));
            Stack stack = new Stack(title, flashcardIds, isPrivate, author); // Set the author when creating the Stack
            int stackId = stackRepository.createStack(stack, flashcardIds, userId, author); // Create the stack with the author

            if (stackId != -1) {
                response.put("success", true);
                response.put("stackId", stackId);
            } else {
                response.put("success", false);
                response.put("message", "Stack konnte nicht erstellt werden.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Ein Fehler ist aufgetreten: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/publicStacks")
    public ResponseEntity<List<Stack>> getPublicStacks() {
        List<Stack> publicStacks = stackRepository.findByIsPrivateFalse();
        return ResponseEntity.ok(publicStacks);
    }

    @GetMapping("/stack/{stackId}")
    public String showStack(@PathVariable("stackId") int stackId, Model model) {
        Stack stack = StackRepository.findStackById(stackId);
        if (stack != null) {
            model.addAttribute("stack", stack);
            model.addAttribute("flashcards", FlashcardRepository.findFlashcardsByStackId(stackId));
            return "flashcards";
        } else {

            return "startseite";
        }
    }

    @GetMapping("/view-stack/{stackId}")
    public String loadStackPage(@PathVariable String stackId, Model model) {
        model.addAttribute("stackId", stackId);
        return "StackFlashcard";
    }

    @GetMapping("/searchPublicStacks")
    public ResponseEntity<List<Stack>> searchPublicStacks(@RequestParam("query") String query) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<Stack> publicStacks = stackRepository.searchPublicStacks(query);
            if (publicStacks.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(publicStacks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/searchPrivateStacks")
    public ResponseEntity<List<Stack>> searchPrivateStacks(@RequestParam("query") String query, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<Stack> privateStacks = stackRepository.searchStacks(query, userId);
            if (privateStacks.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(privateStacks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filterStacks")
    public String filterStacks(@RequestParam(value = "tags", required = false) String tagIds, Model model) {
        System.out.println("Received Tag IDs: " + tagIds);
        if (tagIds == null || tagIds.equals("undefined") || tagIds.trim().isEmpty()) {
            return "startseite";
        }

        List<Integer> tagIdList = Arrays.stream(tagIds.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Stack> filteredStacks = stackRepository.findStacksByTagIds(tagIdList);
        model.addAttribute("stacks", filteredStacks);
        return "startseite";
    }


    @GetMapping("/filterFlashcardsAndStacks")
    public String filterFlashcardsAndStacks(@RequestParam(value = "tags", required = false) String tagIds, Model model, HttpSession session) {
        System.out.println("Empfangene Tag-IDs: " + tagIds);
        if (tagIds == null || tagIds.equals("undefined") || tagIds.trim().isEmpty()) {
            return "startseite";
        }

        List<Integer> tagIdList = Arrays.stream(tagIds.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        List<Flashcard> filteredFlashcards = flashcardRepository.findFlashcardsByTagIds(tagIdList, userId);
        List<Stack> filteredStacks = stackRepository.findStacksByTagIds(tagIdList);
        List<Stack> filteredPublicStacks = stackRepository.findPublicStacksByTagIds(tagIdList);

        model.addAttribute("flashcards", filteredFlashcards);
        model.addAttribute("stacks", filteredStacks);
        model.addAttribute("publicStacks", filteredPublicStacks);
        return "startseite";
    }


}