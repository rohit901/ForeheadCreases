package com.example.facedetectionwrinkle;

import com.google.gson.annotations.SerializedName;


public class User {

    @SerializedName("code")
    private String code;

    @SerializedName("bitsID")
    private String bitsID;

    @SerializedName("age")
    private int age;

    @SerializedName("gender")
    private char gender;

    public User(String code, String bitsID, int age, char gender) {
        this.code = code;
        this.bitsID = bitsID;
        this.age = age;
        this.gender = gender;
    }

    public String getCode() {
        return code;
    }
    public String getBitsID() {
        return bitsID;
    }
    public int getAge() {
        return age;
    }
    public char getGender() {
        return gender;
    }

}
