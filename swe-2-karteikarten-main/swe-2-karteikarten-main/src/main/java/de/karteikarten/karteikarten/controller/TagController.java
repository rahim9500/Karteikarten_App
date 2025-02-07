package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Tag;
import de.karteikarten.karteikarten.repository.TagRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;


@Controller
public class TagController {


    private TagRepository repository = new TagRepository();


    //Navigation


    @GetMapping("/TagVerwalten")
    public String showTagVerwalten(Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/showLogin";
        }

        List<Tag> tags = repository.getAllTagsbyUser(userId);
        model.addAttribute("tags", tags);
        return "TagVerwalten";
    }





    //Methods

    @PostMapping("/createTag")
    public String createTag(@RequestParam("tag") String tagName,
                            @RequestParam("userId") String userId) {

        try {
            repository.createTag(new Tag(tagName));
            int tagId = repository.findTagIdByNames(tagName);
            System.out.println("Tag '" + tagId + "' User '" + userId + "' aktualisiert.");
            repository.assignTagToUser(Integer.parseInt(userId), tagId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "TagVerwalten";
    }




    @PostMapping("/deleteTag")
    public String deleteTag(@RequestParam("tagName") String tagName, RedirectAttributes redirectAttributes) {
        try {
            if (isTagInUse(tagName)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Löschen nicht möglich, da Tag in einer Flashcard verwendet wird.");
            } else {
                repository.deleteTag(tagName);
                redirectAttributes.addFlashAttribute("successMessage", "Tag(s) wurde(n) erfolgreich gelöscht.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fehler beim Löschen des Tags.");
        }
        return "redirect:/TagVerwalten";
    }





    @PostMapping("/editTag")
    public String editTag(@RequestParam("currentTagName") String currentTagName,
                          @RequestParam("newTagName") String newTagName,
                          @RequestParam("userId") String userId
    ) {
        boolean success = repository.updateTag(currentTagName, newTagName, Integer.parseInt(userId));
        if (success) {
            System.out.println("Tag '" + currentTagName + "' erfolgreich zu '" + newTagName + "' aktualisiert.");
            return "redirect:/TagVerwalten";
        } else {
            System.out.println("Fehler beim Aktualisieren des Tags '" + currentTagName + "'.");
            return "redirect:/TagVerwalten";
        }
    }


    @GetMapping("/searchTag")
    public String searchTag(@RequestParam("searchTerm") String searchTerm, Model model, @RequestParam("userId") String userId) {
        List<Tag> foundTags = repository.findTags(searchTerm, Integer.parseInt(userId));
        System.out.println(foundTags);
        model.addAttribute("foundTags", foundTags);
        return "TagVerwalten";
    }



    private boolean isTagInUse(String tagName) {
        String sql = "SELECT COUNT(*) FROM flashcard_tags WHERE tag_id = ?";
        try (Connection conn = database.Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            int tagId = repository.findTagIdByNames(tagName);
            preparedStatement.setInt(1, tagId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            System.out.println("Datenbankfehler beim Überprüfen der Verwendung des Tags: " + e.getMessage());
        }
        return false;
    }


    // Choose Tag
    @GetMapping("/api/tags")
    @ResponseBody
    public ResponseEntity<List<Tag>> getTags(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        List<Tag> tags = repository.getAllTagsbyUser(userId);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/api/getTags")
    @ResponseBody
    public ResponseEntity<List<Tag>> getTags(@RequestParam("userId") int userId) {
        List<Tag> tags = repository.getAllTagsbyUser(userId);
        return ResponseEntity.ok(tags);
    }



}