package com.emich.vibe;

public class Users {

    private String username, email, password, id;

    public Users(String id, String email, String password, String username){
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = id;
    }
    public Users(){

    }

    public String retrieveUid() {
        return id;
    }

    public void setUid(String uid) {
        id = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
