package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.models.Stack;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import de.karteikarten.karteikarten.repository.StackRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private StackRepository stackRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    public HomeController(FlashcardRepository flashcardRepository, StackRepository stackRepository) {
        this.flashcardRepository = flashcardRepository;
        this.stackRepository = stackRepository;
    }

    @GetMapping("/startseite")
    public String startseite(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId != null) {
            List<Flashcard> flashcards = flashcardRepository.getAllFlashcardbyUser(userId);
            List<Stack> stacks = stackRepository.findAllStacks(userId);
            List<Stack> publicStacks = stackRepository.findByIsPrivateFalse();
            model.addAttribute("flashcards", flashcards);
            model.addAttribute("stacks", stacks);
            model.addAttribute("publicStacks", publicStacks);
        }
        return "startseite";
    }
    @PostMapping("/deleteSelectedItems")
    @ResponseBody
    public String deleteSelectedItems(@RequestParam(required = false) String selectedStacks, @RequestParam(required = false) String selectedFlashcards) {
        if (selectedStacks != null) {
            List<Integer> stackIds = Arrays.stream(selectedStacks.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            System.out.println("Stack IDs to delete: " + stackIds);
            stackRepository.deleteAllByIdIn(stackIds);
        }

        if (selectedFlashcards != null) {
            List<Integer> flashcardIds = Arrays.stream(selectedFlashcards.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            System.out.println("Flashcard IDs to delete: " + flashcardIds);
            flashcardRepository.deleteAllByIdIn(flashcardIds);
        }

        return "Selected items deleted successfully.";
    }
}


