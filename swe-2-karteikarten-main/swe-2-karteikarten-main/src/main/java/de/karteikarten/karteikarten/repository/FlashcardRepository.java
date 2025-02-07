package de.karteikarten.karteikarten.repository;

import database.Database;
import de.karteikarten.karteikarten.models.Flashcard;
import de.karteikarten.karteikarten.models.Tag;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class FlashcardRepository {

    public static int createCard(Flashcard card) {
        String sql = "INSERT INTO FLASHCARDS (TITLE, QUESTION, ANSWER) VALUES (?, ?, ?)";
        int insertedId = -1;

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, card.getTitle());
            preparedStatement.setString(2, card.getQuestion());
            preparedStatement.setString(3, card.getAnswer());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        insertedId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        }
        return insertedId;
    }



    public List<Flashcard> getAllFlashcardbyUser(Integer userId) {
        List<Flashcard> flashcards = new ArrayList<>();

        String sql = "SELECT * FROM FLASHCARDS f INNER JOIN USERS_FLASHCARDS uf ON f.ID = uf.FLASHCARD_ID WHERE uf.USER_ID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, String.valueOf(userId));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");

                    Flashcard flashcard = new Flashcard();
                    flashcard.setId(String.valueOf(id));
                    flashcard.setTitle(resultSet.getString("TITLE"));
                    flashcard.setQuestion(resultSet.getString("QUESTION"));
                    flashcard.setAnswer(resultSet.getString("ANSWER"));

                    flashcards.add(flashcard);
                }
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Abrufen aller Karteikarten für Benutzer " + userId + ": " + e.getMessage());
        }

        return flashcards;
    }

    public List<Flashcard> getAllFlashcardbyUserAndStack(Integer userId, int stackId) {
        List<Flashcard> flashcards = new ArrayList<>();

        String sql = "SELECT F.* FROM FLASHCARDS F " +
                "JOIN STACK_FLASHCARDS SF ON F.ID = SF.FLASHCARD_ID " +
                "WHERE SF.STACK_ID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, stackId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String title = resultSet.getString("TITLE");
                    String question = resultSet.getString("QUESTION");
                    String answer = resultSet.getString("ANSWER");

                    Flashcard flashcard = new Flashcard();
                    flashcard.setId(String.valueOf(id));
                    flashcard.setTitle(title);
                    flashcard.setQuestion(question);
                    flashcard.setAnswer(answer);

                    flashcards.add(flashcard);
                }
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Abrufen aller Karteikarten für Stack " + stackId + ": " + e.getMessage());
        }

        return flashcards;
    }




    public boolean updateCard(Flashcard card) {
        String sql = "UPDATE FLASHCARDS SET TITLE = ?, QUESTION = ?, ANSWER = ? WHERE ID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, card.getTitle());
            preparedStatement.setString(2, card.getQuestion());
            preparedStatement.setString(3, card.getAnswer());
            preparedStatement.setInt(4, Integer.parseInt(card.getId()));

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Karteikarte erfolgreich aktualisiert.");
                return true;
            } else {
                System.out.println("Fehler beim Aktualisieren der Karteikarte.");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Datenbankfehler beim Aktualisieren: " + e.getMessage());
        }
        return false;
    }

    public boolean updateFlashcardTags(int flashcardId, int tagId) {
        String sql = "UPDATE FLASHCARD_TAGS SET TAG_ID = ? WHERE FLASHCARD_ID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, String.valueOf(tagId));
            preparedStatement.setString(2, String.valueOf(flashcardId));

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Karteikarte erfolgreich aktualisiert.");
                return true;
            } else {
                System.out.println("Fehler beim Aktualisieren der Karteikarte.");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Datenbankfehler beim Aktualisieren: " + e.getMessage());
        }
        return false;
    }


    public void deleteAllByIdIn(List<Integer> ids) {
        if (ids.isEmpty()) return;

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "DELETE FROM flashcards WHERE id IN (" + placeholders + ");";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (Integer id : ids) {
                pstmt.setInt(index++, id);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting flashcards: " + e.getMessage());
        }
    }


    public void deleteCard(String cardId) {
        try {
            int flashcardId = Integer.parseInt(cardId);

            deleteTagsForFlashcard(flashcardId);

            String sql = "DELETE FROM FLASHCARDS WHERE ID = ?";
            try (Connection conn = Database.connect();
                 PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setInt(1, flashcardId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Datenbankfehler beim Löschen der Karteikarte: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Fehler beim Umwandeln der ID in eine Ganzzahl: " + e.getMessage());
        }
    }

    public void deleteTagsForFlashcard(int flashcardId) {
        String sql = "DELETE FROM FLASHCARD_TAGS WHERE FLASHCARD_ID = ?";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, flashcardId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Löschen der Tags für die Karteikarte: " + e.getMessage());
        }
    }

    public List<String> getTagNamesForFlashcard(int flashcardId) {
        List<String> tagNames = new ArrayList<>();
        String sql = "SELECT t.TITLE FROM TAGS t INNER JOIN FLASHCARD_TAGS ft ON t.ID = ft.TAG_ID WHERE ft.FLASHCARD_ID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, flashcardId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tagNames.add(resultSet.getString("TITLE"));
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Abrufen der Tag-Namen: " + e.getMessage());
        }
        return tagNames;
    }




    public Flashcard findCard(String cardId) {
        String sql = "SELECT * FROM FLASHCARDS WHERE ID = ?";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, Integer.parseInt(cardId));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Flashcard flashcard = new Flashcard();
                flashcard.setId(String.valueOf(resultSet.getInt("ID")));
                flashcard.setTitle(resultSet.getString("TITLE"));
                flashcard.setQuestion(resultSet.getString("QUESTION"));
                flashcard.setAnswer(resultSet.getString("ANSWER"));
                return flashcard;
            }
        } catch (Exception e) {
            System.out.println("Datenbankfehler beim Finden der Karteikarte: " + e.getMessage());
        }
        return null;
    }

    public void assignTagToFlashcard(int flashcardId, int tagIdInt) {
        String sql = "INSERT INTO FLASHCARD_TAGS (FLASHCARD_ID, TAG_ID) VALUES (?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, flashcardId);
            preparedStatement.setInt(2, tagIdInt);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tag erfolgreich der Karteikarte zugeordnet.");
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Zuordnen des Tags: " + e.getMessage());
        }
    }

    public void assignUserFlashcard(int flashcardId, int userId) {
        String sql = "INSERT INTO USERS_FLASHCARDS (USER_ID, FLASHCARD_ID) VALUES (?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, flashcardId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User erfolgreich der Karteikarte zugeordnet.");
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Zuordnen des Tags: " + e.getMessage());
        }
    }

    public int findTagIdByName(String tagName) {
        String query = "SELECT ID FROM TAGS WHERE TITLE = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tagName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }



    public List<Flashcard> findFlashcardsByTagIds(List<Integer> tagIds, int userId) {
        List<Flashcard> flashcards = new ArrayList<>();
        if (tagIds.isEmpty()) {
            return flashcards;
        }

        String inClause = String.join(",", Collections.nCopies(tagIds.size(), "?"));
        String sql = "SELECT f.* FROM FLASHCARDS f " +
                "INNER JOIN FLASHCARD_TAGS ft ON f.ID = ft.FLASHCARD_ID " +
                "INNER JOIN USERS_FLASHCARDS uf ON f.ID = uf.FLASHCARD_ID " +
                "WHERE ft.TAG_ID IN (" + inClause + ") AND uf.USER_ID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            int index = 1;
            for (Integer tagId : tagIds) {
                preparedStatement.setInt(index++, tagId);
            }
            preparedStatement.setInt(index, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Flashcard flashcard = new Flashcard();
                flashcard.setId(String.valueOf(resultSet.getInt("ID")));
                flashcard.setTitle(resultSet.getString("TITLE"));
                flashcard.setQuestion(resultSet.getString("QUESTION"));
                flashcard.setAnswer(resultSet.getString("ANSWER"));
                flashcards.add(flashcard);
            }
        } catch (SQLException e) {
            System.out.println("Database error when retrieving filtered flashcards: " + e.getMessage());
        }
        return flashcards;
    }


    public List<Tag> getAllTagsbyUser(int userId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.* FROM TAGS t INNER JOIN USERS_TAGS ut ON t.ID = ut.TAG_ID WHERE ut.USER_ID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, String.valueOf(userId));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                if (id == 0) {
                    throw new IllegalStateException("Tag ID is 0, which is invalid.");
                }
                String title = rs.getString("title");
                Tag tag = new Tag(id, title);
                tags.add(tag);
            }
        } catch (SQLException e) {
            System.out.println("Database error when retrieving tags: " + e.getMessage());
        }
        return tags;
    }

    public static List<Flashcard> findFlashcardsByStackId(int stackId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT f.* FROM FLASHCARDS f INNER JOIN STACK_FLASHCARDS sf ON f.ID = sf.FLASHCARD_ID WHERE sf.STACK_ID = ?";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, stackId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Flashcard flashcard = new Flashcard();
                flashcard.setId(String.valueOf(resultSet.getInt("ID")));
                flashcard.setTitle(resultSet.getString("TITLE"));
                flashcard.setQuestion(resultSet.getString("QUESTION"));
                flashcard.setAnswer(resultSet.getString("ANSWER"));

                flashcards.add(flashcard);
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Abrufen der Flashcards für den Stack: " + e.getMessage());
        }
        return flashcards;
    }


 }