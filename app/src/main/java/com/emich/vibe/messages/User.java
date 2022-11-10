package com.emich.vibe.messages;

public class User {

    private String name, image;

    public User(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
