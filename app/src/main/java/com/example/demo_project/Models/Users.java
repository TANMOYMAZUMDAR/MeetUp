package com.example.demo_project.Models;

import java.util.Collection;

public class Users {
    String userName;
    String mail;
    String password;
    String userId;
    String lastMessage;
    String ProfilePicture;
    String Interest;
    String about;


    public Users(String userName,String mail,String password,String userId,String lastMessage,String ProfilePicture,String Interest,String about) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.ProfilePicture=ProfilePicture;
        this.Interest=Interest;
        this.about=about;

    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Users(){
    }

    //SignUp Constructor
    public Users(String userName, String mail, String password){
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }


    public String getProfilePicture() {
        return ProfilePicture;
    }
    public String getUserName() {
        return userName;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }


    public String getLastMessage() {
        return lastMessage;
    }
    public String getInterest()
    {
        return Interest;
    }


    public void setProfilePicture(String ProfilePicture) {
        this.ProfilePicture = ProfilePicture;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setInterest(String Interest) {
        this.Interest = Interest;
    }


}
