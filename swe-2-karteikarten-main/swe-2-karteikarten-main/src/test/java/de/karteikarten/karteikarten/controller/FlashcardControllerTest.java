package de.karteikarten.karteikarten.controller;

import database.Database;
import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.repository.FlashcardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.sql.Connection;


@WebMvcTest(FlashcardController.class)
public class FlashcardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlashcardRepository repository;
    @Mock
    private Database database;
    @Mock
    private Connection connection;

    private MockedStatic<Database> mockedDatabase;

    @BeforeEach
    public void setUp() {
        mockedDatabase = Mockito.mockStatic(Database.class);
        Connection mockConnection = Mockito.mock(Connection.class);
        mockedDatabase.when(Database::connect).thenReturn(mockConnection);
    }
    @AfterEach
    public void tearDown() {
        mockedDatabase.close();
    }
    @Test
    public void testShowCreateFlashcardForm() throws Exception {
        mockMvc.perform(get("/createFlashcardForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("KarteikarteErstellen"));
    }

    @Test
    public void testCreateFlashcard() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", new Object());
        try (MockedStatic<FlashcardRepository> mockedRepo = Mockito.mockStatic(FlashcardRepository.class)) {
            mockedRepo.when(() -> FlashcardRepository.createCard(any(Flashcard.class))).thenReturn(1);

            when(FlashcardRepository.createCard(any(Flashcard.class))).thenReturn(1);

            mockMvc.perform(post("/createFlashcard")
                            .param("title", "Test Title")
                            .param("question", "Test Question")
                            .param("answer", "Test Answer")
                            .param("tagsIdString", "Test Tag")
                            .param("userId", "1")
                            .session(session))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/startseite"))
                    .andExpect(flash().attribute("successMessage", "Karteikarte wurde erfolgreich erstellt."));
        }
    }


    @Test
    public void testEditFlashcardFormInvalidId() throws Exception {
        mockMvc.perform(get("/editFlashcardForm/abc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/errorPage"));
    }

    @Test
    public void testCreateFlashcardWithoutLogin() throws Exception {
        mockMvc.perform(post("/createFlashcard")
                        .param("title", "Test Title")
                        .param("question", "Test Question")
                        .param("answer", "Test Answer")
                        .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/showLogin"));
    }
 }
