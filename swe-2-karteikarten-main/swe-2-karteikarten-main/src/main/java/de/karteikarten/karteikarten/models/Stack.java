package de.karteikarten.karteikarten.models;

import java.util.List;

public class Stack {
    private boolean isPrivate;
    private String id;
    private String title;
    private List<String> flashcardIds;
    private List<Flashcard> flashcards; // Hier die Liste von Flashcards hinzufügen
    private String author;

    public Stack(String title, List<String> flashcardIds, boolean isPrivate, String author) {
        this.title = title;
        this.flashcardIds = flashcardIds;
        this.isPrivate = isPrivate;
        this.author = author;
    }
    public boolean isPrivate() {
        return isPrivate;
    }
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Stack() {

    }


    // Getter und Setter für die neue Liste
    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    // bestehende Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }



}
