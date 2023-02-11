package eatoday.com.model;


import java.util.HashMap;
import java.util.Map;

public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private Birthdate birthdate;

    private String avatar;

    public User() {

    }

    public User(String firstName, String lastName, String userName, String email, Birthdate birthdate, String avatar) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.birthdate = birthdate;
        this.avatar = avatar;
    }

public String getAvatar() {
    return avatar;
}

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Birthdate getBirthdate() {
        return birthdate;
    }
}
