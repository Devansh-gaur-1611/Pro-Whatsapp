package com.example.socialmedia.models;

public class Chats {
    public String sender_uId;
    public String receiver_uId;
    public long time;
    public String message;
    public String mobileStoragePosition="";
    public String randomKey;

    public String getRandomKey() {
        return randomKey;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }

    public String getMobileStoragePosition() {
        return mobileStoragePosition;
    }

    public void setMobileStoragePosition(String mobileStoragePosition1) {
        this.mobileStoragePosition = mobileStoragePosition1;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    String ImageUrl;

    public Chats(String sender_uId, String receiver_uId, long time, String message) {
        this.sender_uId = sender_uId;
        this.receiver_uId = receiver_uId;
        this.time = time;
        this.message = message;
    }

    public Chats() {
    }
}
