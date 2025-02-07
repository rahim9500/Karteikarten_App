package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.models.Stack;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import de.karteikarten.karteikarten.repository.StackRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StackApiController {
    @Autowired
    private FlashcardRepository flashcardRepository;
    @Autowired
    private StackRepository stackRepository;

    @GetMapping("/stack/{stackId}/flashcards")
    public ResponseEntity<List<Flashcard>> getFlashcardsByStackId(@PathVariable int stackId) {
        List<Flashcard> flashcards = FlashcardRepository.findFlashcardsByStackId(stackId);
        if (flashcards.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(flashcards);
    }

    @PostMapping("/updateStack/{stackId}")
    public ResponseEntity<Map<String, Object>> updateStack(@PathVariable("stackId") int stackId,
                                                           @RequestBody Map<String, List<String>> payload,
                                                           HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Benutzer ist nicht eingeloggt.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<String> flashcardIds = payload.get("flashcardIds");

        try {
            Stack stack = StackRepository.findStackById(stackId);
            if (stack != null && stackRepository.updateStack(stackId, flashcardIds)) {
                response.put("success", true);
                response.put("message", "Stack erfolgreich aktualisiert.");
            } else {
                response.put("success", false);
                response.put("message", "Stack konnte nicht aktualisiert werden.");
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
}
