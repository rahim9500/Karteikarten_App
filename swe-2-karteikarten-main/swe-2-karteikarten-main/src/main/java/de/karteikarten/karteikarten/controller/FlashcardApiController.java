package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FlashcardApiController {
    @Autowired
    private FlashcardRepository repository;

    @GetMapping("/flashcards")
    @ResponseBody
    public ResponseEntity<List<Flashcard>> getFlashcards(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
        List<Flashcard> flashcards = repository.getAllFlashcardbyUser(userId);
        return ResponseEntity.ok(flashcards);
    }
}
