package com.ral.young.practice.bean;

/**
 * @author renyunhui
 * @date 2022-11-16 14:12
 * @since 1.0.0
 */
public class User {

    private String userName;

    private int age;

    private int gender;

    public User() {

    }

    public User(String userName, int age, int gender) {
        this.userName = userName;
        this.age = age;
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                '}';
    }
}
