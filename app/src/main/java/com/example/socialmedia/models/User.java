package com.example.socialmedia.models;

public class User{
    public String uid;
    public String displayName;
    public String imageUrl;

    public User(String uid, String displayName, String imageUrl) {
        this.uid = uid;
        this.displayName = displayName;
        this.imageUrl = imageUrl;
    }

    public User() {
    }
}
