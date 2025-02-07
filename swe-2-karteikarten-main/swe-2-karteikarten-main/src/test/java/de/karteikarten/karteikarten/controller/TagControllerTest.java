package de.karteikarten.karteikarten.controller;

import de.karteikarten.karteikarten.models.Tag;
import de.karteikarten.karteikarten.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagRepository tagRepository;

    @Test
    public void testShowTagVerwalten() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUserId", 1);
        session.setAttribute("loggedInUser", new Object());
        List<Tag> tags = Arrays.asList(new Tag("Sample Tag"));
        given(tagRepository.getAllTagsbyUser(1)).willReturn(tags);

        mockMvc.perform(get("/TagVerwalten").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("tags"))
                .andExpect(view().name("TagVerwalten"));
    }

    @Test
    public void testShowTagVerwaltenRedirectWhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/TagVerwalten"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/showLogin"));
    }

    @Test
    public void testDeleteTag() throws Exception {
        given(tagRepository.findTagIdByNames("Existing Tag")).willReturn(1);
        given(tagRepository.deleteTag("Existing Tag")).willReturn(true);

        mockMvc.perform(post("/deleteTag")
                        .param("tagName", "Existing Tag"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/TagVerwalten"));
    }

    @Test
    public void testEditTagSuccess() throws Exception {
        given(tagRepository.updateTag("Old Tag", "New Tag", 1)).willReturn(true);

        mockMvc.perform(post("/editTag")
                        .param("currentTagName", "Old Tag")
                        .param("newTagName", "New Tag")
                        .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/TagVerwalten"));
    }

    @Test
    public void testSearchTag() throws Exception {
        List<Tag> foundTags = Arrays.asList(new Tag("Found Tag"));
        given(tagRepository.findTags("search", 1)).willReturn(foundTags);

        mockMvc.perform(get("/searchTag")
                        .param("searchTerm", "search")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("foundTags"))
                .andExpect(view().name("TagVerwalten"));
    }
}
