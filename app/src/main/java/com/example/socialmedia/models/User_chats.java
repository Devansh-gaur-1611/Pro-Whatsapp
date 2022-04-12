package com.example.socialmedia.models;

public class User_chats {
    String uId,message;
    long time;

    public User_chats(String uId, String message, long time) {
        this.uId = uId;
        this.message = message;
        this.time = time;
    }

    public User_chats() {
    }
}
