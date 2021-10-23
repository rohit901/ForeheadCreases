package com.example.facedetectionwrinkle;

import com.google.gson.annotations.SerializedName;

public class Subjects {

    @SerializedName("user_id")
    private String userCode;

    @SerializedName("user")
    private String userCode2;

    @SerializedName("code")
    private String code;

    @SerializedName("count")
    private int count;

    @SerializedName("lastUpdate")
    private String lastUpdate;

    @SerializedName("age")
    private int age;

    @SerializedName("gender")
    private char gender;

    @SerializedName("name")
    private String name;

    public Subjects(String userCode, String code, int count, String lastUpdate, int age, char gender, String name) {
        this.userCode = userCode;
        this.userCode2 = userCode;
        this.code = code;
        this.count = count;
        this.lastUpdate = lastUpdate;
        this.age = age;
        this.gender = gender;
        this.name = name;
    }


    public String getUserCode() {
        return userCode;
    }
    public String getUserCode2() {
        return userCode2;
    }
    public String getName() {
        return name;
    }
    public String getSubjectCode() {
        return code;
    }
    public int getCount() {
        return count;
    }

    public int getAge() {
        return age;
    }
    public char getGender() {
        return gender;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }


}
