package de.karteikarten.karteikarten.repository;

import java.util.ArrayList;
import java.util.List;
import database.Database;
import de.karteikarten.karteikarten.models.Tag;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.*;

@Repository
public class TagRepository {

    public static boolean createTag(Tag tag) {
        if (!tagExists(tag.getTagname())) {
            String sql = "INSERT INTO TAGS (TITLE) VALUES (?)";
            try (Connection conn = Database.connect();
                 PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, tag.getTagname());

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Neuer Tag erfolgreich erstellt und in der Datenbank gespeichert.");
                    return true;
                } else {
                    System.out.println("Fehler beim Erstellen des Tags.");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Datenbankfehler: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Tag mit diesem Namen existiert bereits.");
            return false;
        }
    }


    public static boolean tagExists(String tagname) {
        String sql = "SELECT COUNT(*) FROM TAGS WHERE TITLE = ?";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, tagname);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            System.out.println("Datenbankfehler bei der Überprüfung des Tags: " + e.getMessage());
        }
        return false;
    }


    public boolean deleteTag(String tagName) {
        String sql = "DELETE FROM TAGS WHERE TITLE = ?";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, tagName);

            int rowsDeleted = preparedStatement.executeUpdate();

            return rowsDeleted > 0;
        } catch (Exception e) {
            System.out.println("Datenbankfehler beim Löschen des Tags: " + e.getMessage());
            return false;
        }
    }


    public boolean updateTag(String currentTagName, String newTagName, int userId) {
        String sql = "UPDATE TAGS SET TITLE = ? WHERE ID IN (SELECT TAG_ID FROM USERS_TAGS WHERE USER_ID = ?) AND TITLE = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, newTagName);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, currentTagName);

            int rowsUpdated = preparedStatement.executeUpdate();

            return rowsUpdated > 0;
        } catch (Exception e) {
            System.out.println("Datenbankfehler beim Aktualisieren des Tags: " + e.getMessage());
            return false;
        }
    }


    public List<Tag> findTags(String searchTerm, int userId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM TAGS t INNER JOIN USERS_TAGS ut ON t.ID = ut.TAG_ID WHERE TITLE LIKE ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + searchTerm + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String title = resultSet.getString("TITLE");
                    Tag tag = new Tag(title);
                    tags.add(tag);
                }
            }
        } catch (Exception e) {
            System.out.println("Datenbankfehler bei der Tagsuche: " + e.getMessage());
        }
        return tags;
    }




    public List<Tag> getAllTagsbyUser(int userId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.* FROM TAGS t INNER JOIN USERS_TAGS ut ON t.ID = ut.TAG_ID WHERE ut.USER_ID = ?";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, String.valueOf(userId));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String tagTitle = resultSet.getString("TITLE");
                    Tag tag = new Tag(tagTitle);

                    tag.setTagname(tagTitle);
                    tag.setTitle(tagTitle);

                    tags.add(tag);
                }
            }
        } catch (Exception e) {
            System.out.println("Datenbankfehler beim Abrufen der Tags: " + e.getMessage());
        }
        return tags;
    }


    public void assignTagToUser(int userId, int tagIdInt) {
        String sql = "INSERT INTO USERS_TAGS (USER_ID, TAG_ID) VALUES (?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, tagIdInt);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User erfolgreich der Tag zugeordnet.");
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Zuordnen des User: " + e.getMessage());
        }
    }



    public int findTagIdByNames(String tagName) {
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
}
