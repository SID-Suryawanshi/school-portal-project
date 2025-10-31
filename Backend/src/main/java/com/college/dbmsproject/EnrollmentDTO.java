package com.college.dbmsproject;

// This DTO "flattens" the data for the student's "My Results" page
public class EnrollmentDTO {

    private Integer enrollmentId;
    private String subjectCode;
    private String subjectTitle;
    private String teacherName;
    private String grade;
    private Integer marks;
    private String finalResult;

    // Constructor to map from the Enrollment entity
    public EnrollmentDTO(Enrollment enrollment) {
        this.enrollmentId = enrollment.getId();
        this.grade = enrollment.getGrade();
        this.marks = enrollment.getMarks();
        this.finalResult = enrollment.getFinalResult() != null ? enrollment.getFinalResult().name() : null;

        if (enrollment.getSubject() != null) {
            this.subjectCode = enrollment.getSubject().getSubjectCode();
            this.subjectTitle = enrollment.getSubject().getTitle();

            if (enrollment.getSubject().getTeacherProfile() != null) {
                this.teacherName = enrollment.getSubject().getTeacherProfile().getFirstName() + 
                                   " " + enrollment.getSubject().getTeacherProfile().getLastName();
            }
        }
    }

    // --- Getters ---
    public Integer getEnrollmentId() { return enrollmentId; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectTitle() { return subjectTitle; }
    public String getTeacherName() { return teacherName; }
    public String getGrade() { return grade; }
    public Integer getMarks() { return marks; }
    public String getFinalResult() { return finalResult; }
}