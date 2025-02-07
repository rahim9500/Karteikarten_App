package de.karteikarten.karteikarten.repository;

import database.Database;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
import java.time.LocalDateTime;


@Repository
public class UserRepository {

    public static boolean checkIfTokenExists(String token) {
        String sql = "SELECT COUNT(*) FROM password_reset_tokens WHERE token = ? AND expiry_date > CURRENT_TIMESTAMP";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler beim Überprüfen des Tokens: " + e.getMessage());
        }
        return false;
    }


    public static void updatePasswordUsingToken(String token, String newPassword) {
        String sql = "UPDATE Users SET password = ? WHERE email = (SELECT email FROM password_reset_tokens WHERE token = ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, token);

            int updatedRows = pstmt.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("Passwort erfolgreich aktualisiert.");
            }
        } catch (SQLException e) {
            System.out.println("Fehler beim Aktualisieren des Passworts: " + e.getMessage());
        }
    }



    public void savePasswordResetToken(String email, String token) {
        String sql = "INSERT INTO password_reset_tokens (email, token, expiry_date) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, token);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusHours(24)));

            pstmt.executeUpdate();
            System.out.println("Passwort-Reset-Token gespeichert.");
        } catch (SQLException e) {
            System.out.println("Fehler beim Speichern des Passwort-Reset-Tokens: " + e.getMessage());
        }
    }



    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE EMAIL = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }




    public static void insertUserData(String username, String email, String password) {
        String sql = "INSERT INTO Users (username, email, password) VALUES (?, ?, ?);";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            pstmt.executeUpdate();
            System.out.println("User data inserted into the database.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean userExists(String username, String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? OR email = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static boolean checkLoginCredentials(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?;";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            System.out.println("Query: " + pstmt);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }



    public static Integer getUserId(String username) {
        String sql = "SELECT ID FROM USERS WHERE USERNAME = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                Integer userId = resultSet.getInt("ID");
                System.out.println("Benutzer-ID für " + username + " ist: " + userId);
                return userId;
            } else {
                System.out.println("Kein Benutzer gefunden für: " + username);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler beim Abrufen der Benutzer-ID: " + e.getMessage());
            return null;
        }
    }
}
