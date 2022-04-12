package com.example.socialmedia.models;

import java.util.ArrayList;

public class Post {
    public String text="";
    public User createdBy;
    public long createdAt=0L;
    public ArrayList<String> likedBy=new ArrayList<>();

    public Post(String text, User createdBy, long createdAt, ArrayList<String> likedBy) {
        this.text = text;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.likedBy = likedBy;
    }

    public Post() {
    }
}
