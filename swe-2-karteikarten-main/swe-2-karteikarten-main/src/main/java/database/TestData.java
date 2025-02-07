package database;


import java.sql.Connection;



import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseCreate;

public class TestData {
    public static void insertTestUsers() {
        List<String> sqlStatements = new ArrayList<>();
        // Max Mustermann
        sqlStatements.add("INSERT INTO Users (username, email, password) VALUES ('MaxMustermann', 'max@mustermann.de', '123456');");
        // Maria Musterfrau
        sqlStatements.add("INSERT INTO Users (username, email, password) VALUES ('MariaMusterfrau', 'maria@musterfrau.de', '123456');");
        // Hans Hansen
        sqlStatements.add("INSERT INTO Users (username, email, password) VALUES ('HansHansen', 'hans@hansen.de', '123456');");

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            System.out.println("Test users inserted in the H2 database.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertTestTags() {
        List<String> sqlStatements = new ArrayList<>();
        // Max Mustermann
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Java');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Spring');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('SQL');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('HTML');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('CSS');");

        for (int i = 0; i < 5; i++) {
            sqlStatements.add("INSERT INTO users_tags (user_id, tag_id) VALUES (1, " + (i + 1) + ");");
        }

        // Maria Musterfrau
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Math');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Algebra');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Calculus');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Geometry');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Trigonometry');");

        for (int i = 5; i < 10; i++) {
            sqlStatements.add("INSERT INTO users_tags (user_id, tag_id) VALUES (2, " + (i + 1) + ");");
        }

        // Hans Hansen
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Geography');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Europe');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Asia');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('Africa');");
        sqlStatements.add("INSERT INTO tags (TITLE) VALUES ('America');");

        for (int i = 10; i < 15; i++) {
            sqlStatements.add("INSERT INTO users_tags (user_id, tag_id) VALUES (3, " + (i + 1) + ");");
        }

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            System.out.println("Test tags inserted in the H2 database.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // each user has 10-20 flashcards
    public static void insertTestFlashcards() {
        List<String> sqlStatements = new ArrayList<>();

        // Max Mustermann Flashcards + fitting tags
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Java', 'What is Java?', 'Java is a programming language.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Spring', 'What is Spring?', 'Spring is a Java framework.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('SQL', 'What is SQL?', 'SQL is a programming language.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('HTML', 'What is HTML?', 'HTML is a markup language.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('CSS', 'What is CSS?', 'CSS is a style sheet language.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('JavaScript', 'What is JavaScript?', 'JavaScript is a programming language.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Python', 'What is Python?', 'Python is a programming language.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('C++', 'What is C++?', 'C++ is a programming language.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('C#', 'What is C#?', 'C# is a programming language.');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Ruby', 'What is Ruby?', 'Ruby is a programming language.');");

        for (int i = 0; i < 10; i++) {
            sqlStatements.add("INSERT INTO users_flashcards (user_id, flashcard_id) VALUES (1, " + (i + 1) + ");");
            sqlStatements.add("INSERT INTO flashcard_tags (flashcard_id, tag_id) VALUES (" + (i + 1) + ", " + ((i % 5) + 1) + ");");
        }

        // Maria Musterfrau
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 1+1?', '2');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 2+2?', '4');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 3+3?', '6');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 12*12?', '144');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 123*123?', '15129');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 1234*1234?', '1522756');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 1+2*3?', '7');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is (1+2)*3?', '9');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 1+2*3-4?', '3');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Math', 'What is 1+2*3-4/2?', '6');");

        for (int i = 10; i < 20; i++) {
            sqlStatements.add("INSERT INTO users_flashcards (user_id, flashcard_id) VALUES (2, " + (i + 1) + ");");
            sqlStatements.add("INSERT INTO flashcard_tags (flashcard_id, tag_id) VALUES (" + (i + 1) + ", " + ((i % 5) + 6) + ");");
        }

        // Hans Hansen
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of Germany?', 'Berlin');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of France?', 'Paris');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of Italy?', 'Rome');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of Spain?', 'Madrid');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of Portugal?', 'Lisbon');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of Poland?', 'Warsaw');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of Russia?', 'Moscow');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of China?', 'Beijing');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of Japan?', 'Tokyo');");
        sqlStatements.add("INSERT INTO flashcards (title, question, answer) VALUES ('Geography', 'What is the capital of Australia?', 'Canberra');");

        for (int i = 20; i < 30; i++) {
            sqlStatements.add("INSERT INTO users_flashcards (user_id, flashcard_id) VALUES (3, " + (i + 1) + ");");
            sqlStatements.add("INSERT INTO flashcard_tags (flashcard_id, tag_id) VALUES (" + (i + 1) + ", " + ((i % 5) + 11) + ");");
        }

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            System.out.println("Test flashcards inserted in the H2 database.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTestStacks() {
        List<String> sqlStatements = new ArrayList<>();
        // for each user 2 private stacks and 2 public stacks

        // Max Mustermann
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Programming', false, 'MaxMustermann');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Math', false, 'MaxMustermann');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Java', true, 'MaxMustermann');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Spring', true, 'MaxMustermann');");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (1, 1);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (1, 2);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (1, 3);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (2, 4);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (2, 5);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (2, 6);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (3, 1);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (3, 2);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (3, 3);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (4, 1);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (4, 2);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (4, 3);");

        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (1, 1);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (2, 1);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (3, 1);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (4, 1);");

        // Maria Musterfrau
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Math', false, 'MariaMusterfrau');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Algebra', false, 'MariaMusterfrau');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Calculus', true, 'MariaMusterfrau');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Geometry', true, 'MariaMusterfrau');");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (5, 11);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (5, 12);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (5, 13);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (6, 14);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (6, 15);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (6, 16);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (7, 11);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (7, 12);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (7, 13);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (8, 11);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (8, 12);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (8, 13);");

        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (5, 2);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (6, 2);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (7, 2);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (8, 2);");

        // Hans Hansen
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Geography', false, 'HansHansen');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Europe', false, 'HansHansen');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Asia', true, 'HansHansen');");
        sqlStatements.add("INSERT INTO stacks (title, is_private, author) VALUES ('Africa', true, 'HansHansen');");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (9, 21);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (9, 22);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (9, 23);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (10, 24);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (10, 25);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (10, 26);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (11, 21);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (11, 22);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (11, 23);");

        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (12, 21);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (12, 22);");
        sqlStatements.add("INSERT INTO stack_flashcards (stack_id, flashcard_id) VALUES (12, 23);");

        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (9, 3);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (10, 3);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (11, 3);");
        sqlStatements.add("INSERT INTO users_stacks (stack_id, user_id) VALUES (12, 3);");


        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            System.out.println("Test Stacks inserted in the H2 database.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {
        DatabaseCreate.main(args);

        insertTestUsers();
        insertTestTags();
        insertTestFlashcards();
        createTestStacks();


        System.out.println("-------------------------------------------");
        System.out.println("Tables dropped, Database created & Test data inserted in the H2 database.");
        System.out.println("-------------------------------------------");
        System.out.println("Test ready!");


    }

}
