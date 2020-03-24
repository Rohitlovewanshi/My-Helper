package com.project.myhelper;

public class User {
    public String Email,Name,LockPin;

    public User(){
    }

    public User(String email, String name, String pin) {
        this.Email = email;
        this.Name = name;
        this.LockPin = pin;
    }
}
