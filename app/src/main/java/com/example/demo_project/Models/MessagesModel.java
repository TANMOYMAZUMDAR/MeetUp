package com.example.demo_project.Models;

public class MessagesModel {
    private String  messageId,message,senderId;
    private long timestamp;
    private int feeling=-1;
    private String imageUrl;




    public MessagesModel( String message, String senderId, long timestamp) {

        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;

    }
    public MessagesModel()
    {

    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }
}