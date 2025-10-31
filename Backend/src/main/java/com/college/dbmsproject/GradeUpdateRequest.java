package com.college.dbmsproject;

import com.college.dbmsproject.Enrollment.FinalResult;

// This DTO carries the data for a grade update
public class GradeUpdateRequest {
    private String grade;
    private Integer marks;
    private FinalResult finalResult;

    // Getters and Setters
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