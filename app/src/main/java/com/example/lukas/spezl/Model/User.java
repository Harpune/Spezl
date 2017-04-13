package com.example.lukas.spezl.Model;

public class User {

    private String userId;
    private String username;
    private Boolean sex;
    private String town;
    private String email;
    private Double age;
    private String imageUri;

    public User(){}

    public User(String userId, String username, Boolean sex, String town, String email, Double age, String imageUri) {
        this.userId = userId;
        this.username = username;
        this.sex = sex;
        this.town = town;
        this.email = email;
        this.age = age;
        this.imageUri = imageUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", sex=" + sex +
                ", town='" + town + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", imageUri='" + imageUri + '\'' +
                '}';
    }
}
