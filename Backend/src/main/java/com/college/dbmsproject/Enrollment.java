package com.college.dbmsproject;

import jakarta.persistence.*;

/**
 * The v3.0 Enrollment model. This is the join table.
 */
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // --- Relationships ---
    
    // Many enrollments for one student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Many enrollments for one subject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // --- Data Fields ---
    private String grade;
    private Integer marks;

    @Enumerated(EnumType.STRING)
    private FinalResult finalResult;

    public enum FinalResult {
        PASS,
        FAIL
    }
    
    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public User getStudent() {
        return student;
    }
    public void setStudent(User student) {
        this.student = student;
    }
    public Subject getSubject() {
        return subject;
    }
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public Integer getMarks() {
        return marks;
    }
    public void setMarks(Integer marks) {
        this.marks = marks;
    }
    public FinalResult getFinalResult() {
        return finalResult;
    }
    public void setFinalResult(FinalResult finalResult) {
        this.finalResult = finalResult;
    }
}
