package com.college.dbmsproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Make sure this is imported

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    
    // --- NEW ---
    // Find all subjects taught by a specific teacher's profile
    List<Subject> findByTeacherProfile(Profile teacherProfile);
}