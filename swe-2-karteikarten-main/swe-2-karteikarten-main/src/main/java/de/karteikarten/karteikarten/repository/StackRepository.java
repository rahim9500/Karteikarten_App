package de.karteikarten.karteikarten.repository;

import database.Database;
import de.karteikarten.karteikarten.models.Stack;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class StackRepository {


    public void deleteAllByIdIn(List<Integer> ids) {
        String idString = ids.toString().replace("[", "").replace("]", "");
        String sql = "DELETE FROM stacks WHERE id IN (" + idString + ");";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Number of affected rows: " + affectedRows);
        } catch (SQLException e) {
            System.out.println("Error deleting stacks: " + e.getMessage());
        }
    }

    public int createStack(Stack stack, List<String> flashcardIds, Integer userId, String author) {
        int stackId = -1;
        stack.setAuthor(author);
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatementStack = conn.prepareStatement(
                     "INSERT INTO STACKS (TITLE, IS_PRIVATE, AUTHOR) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement preparedStatementRelation = conn.prepareStatement(
                     "INSERT INTO STACK_FLASHCARDS (STACK_ID, FLASHCARD_ID) VALUES (?, ?)");
             PreparedStatement preparedStatementUserStack = conn.prepareStatement(
                     "INSERT INTO USERS_STACKS (USER_ID, STACK_ID) VALUES (?, ?)");
             PreparedStatement preparedStatementUser = conn.prepareStatement(
                     "SELECT USERNAME FROM USERS WHERE ID = ?")) {

            conn.setAutoCommit(false);

            preparedStatementUser.setInt(1, userId);
            ResultSet resultSet = preparedStatementUser.executeQuery();
            String username = resultSet.next() ? resultSet.getString("USERNAME") : null;

            preparedStatementStack.setString(1, stack.getTitle());
            preparedStatementStack.setBoolean(2, stack.isPrivate());
            preparedStatementStack.setString(3, username);
            System.out.println(stack.getTitle());
            System.out.println(stack.isPrivate());
            System.out.println(username);

            int affectedRows = preparedStatementStack.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Erstellen des Stacks fehlgeschlagen, keine Zeilen betroffen.");
            }

            try (ResultSet generatedKeys = preparedStatementStack.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stackId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Erstellen des Stacks fehlgeschlagen, keine generierte ID erhalten.");
                }
            }

            for (String flashcardId : flashcardIds) {
                preparedStatementRelation.setInt(1, stackId);
                preparedStatementRelation.setInt(2, Integer.parseInt(flashcardId));
                preparedStatementRelation.executeUpdate();
            }

            preparedStatementUserStack.setInt(1, userId);
            preparedStatementUserStack.setInt(2, stackId);
            preparedStatementUserStack.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = Database.connect()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return stackId;
    }


    public List<Stack> findAllStacks(Integer userId) {
        List<Stack> stacks = new ArrayList<>();
        String sql = "SELECT s.* FROM stacks s JOIN users_stacks us ON s.id = us.stack_id WHERE us.user_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = Integer.toString(rs.getInt("id"));
                    String title = rs.getString("title");
                    boolean isPrivate = rs.getBoolean("is_private");
                    List<String> flashcards = new ArrayList<>();
                    String author = rs.getString("author");
                    Stack stack = new Stack(title, flashcards, isPrivate, author);
                    stack.setId(id);
                    stacks.add(stack);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching stacks: " + e.getMessage());
        }
        return stacks;
    }

    public static Stack findStackById(int stackId) {
        String sql = "SELECT * FROM STACKS WHERE ID = ?";
        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, stackId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Stack stack = new Stack();
                stack.setId(String.valueOf(resultSet.getInt("ID")));
                stack.setTitle(resultSet.getString("TITLE"));
                return stack;
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Finden des Stacks: " + e.getMessage());
        }
        return null;
    }

    //mach eine neue methode die alle stacks zurückgibt die bei is_private Fasle haben
    /*public List<Stack> findpublicStacks() {
        List<Stack> stacks = new ArrayList<>();
        String sql = "SELECT * FROM STACKS WHERE IS_PRIVATE = FALSE";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = Integer.toString(rs.getInt("id"));
                String title = rs.getString("title");
                boolean isPrivate = rs.getBoolean("is_private");
                List<String> flashcards = new ArrayList<>();
                Stack stack = new Stack(title, flashcards, isPrivate);
                stack.setId(id);
                stacks.add(stack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching public stacks: " + e.getMessage());
        }
        return stacks;
    } */

    public List<Stack> findPublicStacks() {
        List<Stack> stacks = new ArrayList<>();
        String sql = "SELECT S.ID, S.TITLE, S.AUTHOR " +
                "FROM STACKS S " +
                "JOIN USERS_STACKS US ON S.ID = US.STACK_ID " +
                "WHERE S.IS_PRIVATE = FALSE";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Stack stack = new Stack();
                stack.setId(rs.getString("id"));
                stack.setTitle(rs.getString("title"));
                stack.setAuthor(rs.getString("author"));
                stacks.add(stack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching public stacks: " + e.getMessage());
        }
        return stacks;
    }



    public List<Stack> searchStacks(String query, Integer userId) {
        List<Stack> results = new ArrayList<>();
        if (query == null || query.trim().length() < 2) {
            return results; // Rückgabe einer leeren Liste, wenn die Suchanfrage nicht gültig ist
        }

        String sql = "SELECT * FROM stacks s JOIN users_stacks us ON s.id = us.stack_id WHERE us.user_id = ? AND LOWER(s.title) LIKE LOWER(?)";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Stack stack = new Stack(rs.getString("title"), new ArrayList<>(), rs.getBoolean("is_private"), rs.getString("author"));
                stack.setId(Integer.toString(rs.getInt("id")));
                results.add(stack);
            }
        } catch (SQLException e) {
            System.out.println("Error searching private stacks: " + e.getMessage());
        }
        return results;
    }



    public static boolean updateStack(Integer stackId, List<String> flashcardIds) {
        Connection conn = null;
        try {
            conn = Database.connect();
            conn.setAutoCommit(false);

            String stackIdStr = stackId.toString();
            System.out.println("Updating stack with ID: " + stackIdStr);
            System.out.println("New flashcard IDs: " + flashcardIds);

            String deleteSql = "DELETE FROM STACK_FLASHCARDS WHERE STACK_ID = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, stackIdStr);
                deleteStmt.executeUpdate();
            }

            String insertSql = "INSERT INTO STACK_FLASHCARDS (STACK_ID, FLASHCARD_ID) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (String flashcardId : flashcardIds) {
                    insertStmt.setString(1, stackIdStr);
                    insertStmt.setInt(2, Integer.parseInt(flashcardId));
                    insertStmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public List<Stack> findByIsPrivateTrue() {
        List<Stack> stacks = new ArrayList<>();
        String sql = "SELECT * FROM STACKS WHERE IS_PRIVATE = TRUE";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = Integer.toString(rs.getInt("id"));
                String title = rs.getString("title");
                boolean isPrivate = rs.getBoolean("is_private");
                String author = rs.getString("author");
                List<String> flashcards = new ArrayList<>();
                Stack stack = new Stack(title, flashcards, isPrivate, author);
                stack.setId(id);
                stacks.add(stack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching private stacks: " + e.getMessage());
        }
        return stacks;
    }

    /*public List<Stack> findByIsPrivateFalse() {
        List<Stack> stacks = new ArrayList<>();
        String sql = "SELECT * FROM STACKS WHERE IS_PRIVATE = FALSE";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = Integer.toString(rs.getInt("id"));
                String title = rs.getString("title");
                boolean isPrivate = rs.getBoolean("is_private");
                List<String> flashcards = new ArrayList<>();
                Stack stack = new Stack(rs.getString("title"), new ArrayList<String>(), rs.getBoolean("is_private"), rs.getString("author"));
                stack.setId(id);
                stacks.add(stack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching public stacks: " + e.getMessage());
        }
        return stacks;
    }*/

    public List<Stack> searchPublicStacks(String query) {
        List<Stack> results = new ArrayList<>();
        if (query == null || query.trim().length() < 2) {
            return results;
        }

        String sql = "SELECT * FROM stacks WHERE is_private = false AND LOWER(title) LIKE LOWER(?)";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Stack stack = new Stack(rs.getString("title"), new ArrayList<>(), rs.getBoolean("is_private"), rs.getString("author"));
                stack.setId(Integer.toString(rs.getInt("id")));
                results.add(stack);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching public stacks: " + e.getMessage());
        }
        return results;
    }

    public List<Stack> findStacksByTagIds(List<Integer> tagIds) {
        List<Stack> stacks = new ArrayList<>();
        if (tagIds.isEmpty()) {
            return stacks;
        }

        String inClause = String.join(",", Collections.nCopies(tagIds.size(), "?"));
        String sql = "SELECT DISTINCT s.* FROM STACKS s " +
                "INNER JOIN STACK_FLASHCARDS sf ON s.ID = sf.STACK_ID " +
                "INNER JOIN FLASHCARD_TAGS ft ON sf.FLASHCARD_ID = ft.FLASHCARD_ID " +
                "WHERE ft.TAG_ID IN (" + inClause + ") AND s.IS_PRIVATE = true";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            int index = 1;
            for (Integer tagId : tagIds) {
                preparedStatement.setInt(index++, tagId);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Stack stack = new Stack();
                stack.setId(String.valueOf(resultSet.getInt("ID")));
                stack.setTitle(resultSet.getString("TITLE"));
                stack.setPrivate(resultSet.getBoolean("IS_PRIVATE"));
                stacks.add(stack);

                System.out.println(stack.getTitle());
            }
        } catch (SQLException e) {
            System.out.println("Database error when retrieving stacks by tag IDs: " + e.getMessage());
        }

        return stacks;
    }

    public List<Stack> findPublicStacksByTagIds(List<Integer> tagIds) {
        List<Stack> stacks = new ArrayList<>();
        if (tagIds.isEmpty()) {
            return stacks;
        }

        String inClause = String.join(",", Collections.nCopies(tagIds.size(), "?"));
        String sql = "SELECT DISTINCT s.* FROM STACKS s " +
                "INNER JOIN STACK_FLASHCARDS sf ON s.ID = sf.STACK_ID " +
                "INNER JOIN FLASHCARD_TAGS ft ON sf.FLASHCARD_ID = ft.FLASHCARD_ID " +
                "WHERE ft.TAG_ID IN (" + inClause + ") AND s.IS_PRIVATE = false";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            int index = 1;
            for (Integer tagId : tagIds) {
                preparedStatement.setInt(index++, tagId);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Stack stack = new Stack();
                stack.setId(String.valueOf(resultSet.getInt("ID")));
                stack.setTitle(resultSet.getString("TITLE"));
                stack.setPrivate(resultSet.getBoolean("IS_PRIVATE"));
                stack.setAuthor(resultSet.getString("AUTHOR")); // Retrieve the author field
                stacks.add(stack);
            }
        } catch (SQLException e) {
            System.out.println("Database error when retrieving filtered public stacks: " + e.getMessage());
        }

        return stacks;
    }

    public List<Stack> findByIsPrivateFalse() {
        List<Stack> stacks = new ArrayList<>();
        String sql = "SELECT * FROM STACKS WHERE is_private = false";

        try (Connection conn = Database.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Stack stack = new Stack();
                stack.setId(resultSet.getString("ID"));
                stack.setTitle(resultSet.getString("TITLE"));
                stack.setPrivate(resultSet.getBoolean("is_private"));
                stack.setAuthor(resultSet.getString("author"));
                stacks.add(stack);

            }
        } catch (SQLException e) {
            System.out.println("Database error when retrieving public stacks: " + e.getMessage());
        }
        return stacks;
    }

}
