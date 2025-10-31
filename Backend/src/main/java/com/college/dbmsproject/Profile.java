package com.college.dbmsproject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Important: Prevents infinite loops when sending JSON
    private User user;

    private String firstName;
    private String lastName;
    private int age;
    private String mobile;
    private String address;
    private String parentMobile; // This will be null for teachers

    // --- CONSTRUCTORS ---

    // 1. An empty constructor is required by JPA/Hibernate
    public Profile() {
    }

    // 2. This is the constructor our controllers NEED.
    // This is the fix for your error.
    public Profile(User user, String firstName, String lastName, int age, String mobile, String address, String parentMobile) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.mobile = mobile;
        this.address = address;
        this.parentMobile = parentMobile;
    }


    // --- GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParentMobile() {
        return parentMobile;
    }

    public void setParentMobile(String parentMobile) {
        this.parentMobile = parentMobile;
    }
}