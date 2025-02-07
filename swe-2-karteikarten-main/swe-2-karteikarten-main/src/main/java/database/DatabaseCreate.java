package database;


import java.sql.Connection;



import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCreate {

    public static void dropTables() {
        List<String> sqlStatements = new ArrayList<>();
        sqlStatements.add("DROP TABLE IF EXISTS users_stacks;");
        sqlStatements.add("DROP TABLE IF EXISTS stacks_users;");
        sqlStatements.add("DROP TABLE IF EXISTS stack_flashcards;");
        sqlStatements.add("DROP TABLE IF EXISTS stacks;");
        sqlStatements.add("DROP TABLE IF EXISTS password_reset_tokens;");
        sqlStatements.add("DROP TABLE IF EXISTS users_flashcards;");
        sqlStatements.add("DROP TABLE IF EXISTS users_tags;");
        sqlStatements.add("DROP TABLE IF EXISTS Users;");
        sqlStatements.add("DROP TABLE IF EXISTS flashcard_tags;");
        sqlStatements.add("DROP TABLE IF EXISTS Tags;");
        sqlStatements.add("DROP TABLE IF EXISTS flashcards;");

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            System.out.println("Tables dropped in the H2 database.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable() {
        List<String> sqlStatements = new ArrayList<>();
        sqlStatements.add("CREATE TABLE IF NOT EXISTS flashcards (\n"
                + " id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + " title varchar(255) not null,\n"
                + " question text,\n"
                + " answer text\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS Tags (\n"
                + " id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + " title VARCHAR(255) NOT NULL\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS flashcard_tags (\n"
                + " flashcard_id INT,\n"
                + " tag_id INT,\n"
                + " PRIMARY KEY (flashcard_id, tag_id),\n"
                + " FOREIGN KEY (flashcard_id) REFERENCES flashcards(id) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + " FOREIGN KEY (tag_id) REFERENCES tags(id) ON UPDATE CASCADE\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS Users (\n"
                + " id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + " username VARCHAR(255) NOT NULL,\n"
                + " email VARCHAR(255) NOT NULL,\n"
                + " password VARCHAR(255) NOT NULL\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS users_tags (\n"
                + " user_id INT,\n"
                + " tag_id INT,\n"
                + " PRIMARY KEY (user_id, tag_id),\n"
                + " FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + " FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS users_flashcards (\n"
                + " user_id INT,\n"
                + " flashcard_id INT,\n"
                + " PRIMARY KEY (user_id, flashcard_id),\n"
                + " FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + " FOREIGN KEY (flashcard_id) REFERENCES flashcards(id) ON DELETE CASCADE ON UPDATE CASCADE\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS password_reset_tokens (\n"
                + " email VARCHAR(255),\n"
                + " token VARCHAR(255), \n"
                + " expiry_date TIMESTAMP \n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS stacks (\n"
                + " id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + " title VARCHAR(255) NOT NULL\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS stack_flashcards (\n"
                + " stack_id INT,\n"
                + " flashcard_id INT,\n"
                + " PRIMARY KEY (stack_id, flashcard_id),\n"
                + " FOREIGN KEY (stack_id) REFERENCES stacks(id) ON DELETE CASCADE,\n"
                + " FOREIGN KEY (flashcard_id) REFERENCES flashcards(id) ON DELETE CASCADE\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS stacks_users (\n"
                + " stack_id INT,\n"
                + " user_id INT,\n"
                + " PRIMARY KEY (stack_id, user_id),\n"
                + " FOREIGN KEY (stack_id) REFERENCES stacks(id) ON DELETE CASCADE,\n"
                + " FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE\n"
                + ");");
        sqlStatements.add("CREATE TABLE IF NOT EXISTS users_stacks (\n"
                + " user_id INT,\n"
                + " stack_id INT,\n"
                + " PRIMARY KEY (user_id, stack_id),\n"
                + " FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + " FOREIGN KEY (stack_id) REFERENCES stacks(id) ON DELETE CASCADE ON UPDATE CASCADE\n"
                + ");");
        sqlStatements.add("ALTER TABLE stacks ADD COLUMN IF NOT EXISTS is_private BOOLEAN NOT NULL DEFAULT FALSE;");
        sqlStatements.add("ALTER TABLE stacks ADD COLUMN IF NOT EXISTS author VARCHAR(255);");
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            System.out.println("Tables created in the H2 database.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public static void main(String[] args) {
        dropTables();
        createNewTable();

    }
}
