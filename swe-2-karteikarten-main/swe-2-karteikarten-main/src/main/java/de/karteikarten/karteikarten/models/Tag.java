package de.karteikarten.karteikarten.models;

public class Tag {

    private String tagname;

    public Tag(String tagname) {
        this.tagname = tagname;
    }


    public String getTagname() {
        return tagname;
    }

    public void setTagname(String newTagName) {
        this.tagname = newTagName;
    }

    private int id;
    private String title;

    public Tag(int id, String title) {
        this.id = id;
        this.title = title;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}