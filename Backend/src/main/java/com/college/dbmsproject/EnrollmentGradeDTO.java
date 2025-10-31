package com.college.dbmsproject;

import com.college.dbmsproject.Enrollment.FinalResult;

// This DTO "flattens" data for the teacher's gradebook
public class EnrollmentGradeDTO {

    private Integer enrollmentId;
    private Long studentUserId; // So the teacher can see/manage the student
    private String studentName;
    private String studentEmail;
    
    // The grade data
    private String grade;
    private Integer marks;
    private FinalResult finalResult;

    public EnrollmentGradeDTO(Enrollment enrollment) {
        this.enrollmentId = enrollment.getId();
        this.grade = enrollment.getGrade();
        this.marks = enrollment.getMarks();
        this.finalResult = enrollment.getFinalResult();

        if (enrollment.getStudent() != null) {
            this.studentUserId = enrollment.getStudent().getId();
            this.studentEmail = enrollment.getStudent().getEmail();
            
            if (enrollment.getStudent().getProfile() != null) {
                this.studentName = enrollment.getStudent().getProfile().getFirstName() + 
                                   " " + enrollment.getStudent().getProfile().getLastName();
            }
        }
    }

    // --- Getters ---
    public Integer getEnrollmentId() { return enrollmentId; }
    public Long getStudentUserId() { return studentUserId; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getGrade() { return grade; }
    public Integer getMarks() { return marks; }
    public FinalResult getFinalResult() { return finalResult; }
}