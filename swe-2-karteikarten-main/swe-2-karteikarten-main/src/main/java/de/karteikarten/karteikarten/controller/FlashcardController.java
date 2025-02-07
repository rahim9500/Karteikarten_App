package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.models.Tag;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class FlashcardController {

    private FlashcardRepository repository = new FlashcardRepository();


    @GetMapping("/createFlashcardForm")
    public String showCreateFlashcardForm() {
        return "KarteikarteErstellen";
    }

    @GetMapping("/editFlashcardForm")
    public String editCreateFlashcardForm() {
        return "KarteikarteBearbeiten";
    }

    @GetMapping("/showLogin")
    public String showLogin() {
        return "Login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "Registrierung";
    }

    @GetMapping("/editCardForm")
    public String editCard() {
        return "TagVerwalten";
    }

    @GetMapping("/flashcard/{id}")
    public String flashcardView(@PathVariable String id, Model model) {
        Flashcard flashcard = repository.findCard(id);
        if (flashcard != null) {
            List<String> tagNames = repository.getTagNamesForFlashcard(Integer.parseInt(id));
            model.addAttribute("flashcard", flashcard);
            model.addAttribute("tagNames", tagNames);

            return "KarteikartenView";
        } else {
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/flashcardStack/{id}")
    public String flashcardStackView(@PathVariable String id, @RequestParam(required = false) Integer stackId, Model model) {
        Flashcard flashcard = repository.findCard(id);
        if (flashcard != null) {
            List<String> tagNames = repository.getTagNamesForFlashcard(Integer.parseInt(id));
            model.addAttribute("flashcard", flashcard);
            model.addAttribute("tagNames", tagNames);
            model.addAttribute("stackId", stackId); // Weiterreichen der stackId an die View
            return "StackFlashcard";
        } else {
            return "redirect:/errorPage";
        }
    }








    //Methods


    @PostMapping("/createFlashcard")
    public String createFlashcard(@RequestParam("title") String title,
                                  @RequestParam("question") String question,
                                  @RequestParam("answer") String answer,
                                  @RequestParam(value = "tagIdsString", required = false) String tagIdsString,
                                  @RequestParam("userId") String userId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/showLogin";
        }

        Flashcard flashcard = new Flashcard(title, question, answer);
        int flashcardId = repository.createCard(flashcard);

        System.out.println("User ID: " + userId);

        if (flashcardId != -1 && tagIdsString != null && !tagIdsString.isEmpty()) {
            String[] tagNames = tagIdsString.split(",");
            for (String tagName : tagNames) {
                int tagId = repository.findTagIdByName(tagName.trim());
                if (tagId != -1) {
                    repository.assignTagToFlashcard(flashcardId, tagId);
                    repository.assignUserFlashcard(flashcardId, Integer.parseInt(userId));
                }
            }
        }
        redirectAttributes.addFlashAttribute("successMessage", "Karteikarte wurde erfolgreich erstellt.");
        return "redirect:/startseite";
    }

    @PostMapping("/editFlashcard")
    public String updateFlashcard(@ModelAttribute("flashcard") Flashcard flashcard,
                                  @RequestParam(value = "tagIdsString", required = false) String tagIdsString,
                                  RedirectAttributes redirectAttributes) {
        if (flashcard.getId() == null || flashcard.getId().isEmpty()) {
            return "redirect:/errorPage";
        }
        System.out.println("Flashcard ID: " + flashcard.getId());
        System.out.println("Flashcard ID: " + tagIdsString);

        boolean success = repository.updateCard(flashcard);
        if (success) {
            if (tagIdsString != null && !tagIdsString.isEmpty()) {
                String[] tagNames = tagIdsString.split(",");
                if (tagNames.length > 0) {
                    String firstTagName = tagNames[0].trim();
                    int tagId = repository.findTagIdByName(firstTagName);
                    System.out.println("Flashcard ID: " + tagId);
                    if (tagId != -1) {
                        repository.updateFlashcardTags(Integer.parseInt(flashcard.getId()), tagId);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Bearbeitung der Karteikarte abgeschlossen.");
            return "redirect:/flashcard/" + flashcard.getId();
        } else {
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/filterFlashcards")
    public String filterFlashcards(@RequestParam(value = "tags", required = false) String tagIds, Model model, HttpSession session) {
        System.out.println("Empfangene Tag-IDs: " + tagIds);
        if (tagIds == null || tagIds.equals("undefined") || tagIds.trim().isEmpty()) {
            return "startseite";
        }

        List<Integer> tagIdList = Arrays.stream(tagIds.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        List<Flashcard> filteredFlashcards = repository.findFlashcardsByTagIds(tagIdList, userId);
        model.addAttribute("flashcards", filteredFlashcards);
        return "startseite";
    }



    @GetMapping("/editFlashcardForm/{id}")
    public String editFlashcardForm(@PathVariable String id, Model model) {
        try {
            long flashcardId = Long.parseLong(id);
            Flashcard flashcard = repository.findCard(String.valueOf(flashcardId));

            if (flashcard != null) {
                model.addAttribute("flashcard", flashcard);
                return "KarteikarteBearbeiten";
            } else {
                return "redirect:/errorPage";
            }
        } catch (NumberFormatException e) {
            return "redirect:/errorPage";
        }
    }



    @PostMapping("/deleteSelectedFlashcards")
    public String deleteSelectedFlashcards(@RequestParam("selectedCards") List<String> selectedCards,
                                           RedirectAttributes redirectAttributes) {
        for (String cardId : selectedCards) {
            repository.deleteCard(cardId);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Karteikarte(n) wurde(n) erfolgreich gelöscht.");
        return "redirect:/startseite";
    }

    private int currentIndex = 0;

    @GetMapping("/navigateFlashcard")
    public String navigateFlashcard(Model model, HttpSession session, @RequestParam String direction) {

        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        List<Flashcard> flashcards = repository.getAllFlashcardbyUser(userId);

        if (flashcards.isEmpty()) {
            return "NoFlashcardsView";
        }

        if ("next".equals(direction)) {
            currentIndex = (currentIndex + 1) % flashcards.size();
        } else if ("previous".equals(direction)) {
            currentIndex = (currentIndex - 1 + flashcards.size()) % flashcards.size();
        }

        Flashcard currentFlashcard = flashcards.get(currentIndex);
        List<String> tagNames = repository.getTagNamesForFlashcard(Integer.parseInt(currentFlashcard.getId()));

        model.addAttribute("flashcard", currentFlashcard);
        model.addAttribute("tagNames", tagNames);

        return "KarteikartenView";
    }


    @PostMapping("/navigateStackFlashcard")
    public String navigateStackFlashcard(@RequestParam("stackId") Integer stackId,
                                         @RequestParam("userId") String userId,
                                         @RequestParam("direction") String direction,
                                         Model model) {

        model.addAttribute("stackId", stackId);
        model.addAttribute("userId", userId);
        List<Flashcard> flashcards = repository.getAllFlashcardbyUserAndStack(Integer.valueOf(userId), stackId);


        if (flashcards.isEmpty()) {
            return "NoFlashcardsView";
        }

        if ("next".equals(direction)) {
            currentIndex = (currentIndex + 1) % flashcards.size();
        } else if ("previous".equals(direction)) {
            currentIndex = (currentIndex - 1 + flashcards.size()) % flashcards.size();
        }

        Flashcard currentFlashcard = flashcards.get(currentIndex);
        List<String> tagNames = repository.getTagNamesForFlashcard(Integer.parseInt(currentFlashcard.getId()));

        model.addAttribute("flashcard", currentFlashcard);
        model.addAttribute("tagNames", tagNames);
        model.addAttribute("stackId", stackId);
        model.addAttribute("userId", userId);

        return "StackFlashcard";
    }



    @GetMapping("/api/flashcard-tags")
    @ResponseBody
    public ResponseEntity<List<Tag>> getFlashcardTags( HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        List<Tag> tags = repository.getAllTagsbyUser(userId);
        return ResponseEntity.ok(tags);
    }

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