package com.college.dbmsproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    // (This one is for students)
    @Query("SELECT e FROM Enrollment e " +
           "JOIN FETCH e.subject s " +
           "JOIN FETCH s.teacherProfile " +
           "WHERE e.student = :student")
    List<Enrollment> findByStudentWithDetails(@Param("student") User student);

    Optional<Enrollment> findByStudentAndSubject(User student, Subject subject);

    // --- NEW ---
    // (This one is for teachers)
    // Find all enrollments for a subject, and also get the student's User and Profile
    @Query("SELECT e FROM Enrollment e " +
           "JOIN FETCH e.student s " +
           "JOIN FETCH s.profile " +
           "WHERE e.subject = :subject")
    List<Enrollment> findBySubjectWithStudentDetails(@Param("subject") Subject subject);
}