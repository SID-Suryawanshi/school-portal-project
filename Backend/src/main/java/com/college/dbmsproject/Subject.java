package com.college.dbmsproject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    // --- ADD THIS ---
    @Column(nullable = false, unique = true) // Making it required and unique
    private String subjectCode;
    // --- END ADD ---

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "teacher_profile_id", nullable = false)
    private Profile teacherProfile;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Prevent loops when fetching subjects
    private Set<Enrollment> enrollments;


    // --- Getters and Setters ---
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    // --- ADD THIS ---
    public String getSubjectCode() {
        return subjectCode;
    }
    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
    // --- END ADD ---

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Profile getTeacherProfile() {
        return teacherProfile;
    }
    public void setTeacherProfile(Profile teacherProfile) {
        this.teacherProfile = teacherProfile;
    }
    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }
    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }
}