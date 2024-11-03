package com.example.qlct.Model;

public class User {
    private String name;
    private String phone;
    private int age;
    private String gmail;
    private String password;

    // Constructor (Hàm khởi tạo)
    public User(String name, String phone, int age, String gmail, String password) {
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.gmail = gmail;
        this.password = password;
    }

    // Getters và Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                ", gmail='" + gmail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

