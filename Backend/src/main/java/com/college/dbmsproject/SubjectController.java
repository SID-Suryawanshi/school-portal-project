package com.college.dbmsproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * v3.2 NEW FILE
 * This controller handles all API requests for creating,
 * reading, and deleting subjects.
 * It is protected by SecurityConfig to be TEACHER-only.
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    // GET all subjects
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        // Find all subjects. Thanks to EAGER fetching on the 'teacherProfile'
        // in Subject.java, the teacher's info (including name and email)
        // will be included automatically.
        List<Subject> subjects = subjectRepository.findAll();
        return ResponseEntity.ok(subjects);
    }

    // POST a new subject
    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody Subject subjectRequest, @AuthenticationPrincipal UserDetails userDetails) {
        
        // Find the profile of the currently logged-in teacher
        User teacherUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile teacherProfile = profileRepository.findByUser(teacherUser)
                .orElseThrow(() -> new RuntimeException("Profile not found for user"));

        // Set the teacher's profile on the new subject
        subjectRequest.setTeacherProfile(teacherProfile);
        
        Subject savedSubject = subjectRepository.save(subjectRequest);
        return ResponseEntity.ok(savedSubject);
    }

    // DELETE a subject
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        
        // Find the profile of the currently logged-in teacher
        User teacherUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile teacherProfile = profileRepository.findByUser(teacherUser)
                .orElseThrow(() -> new RuntimeException("Profile not found for user"));

        // Find the subject to be deleted
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (!subjectOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Subject subject = subjectOpt.get();

        // Security check: Only the teacher who created the subject can delete it
        if (!subject.getTeacherProfile().getId().equals(teacherProfile.getId())) {
            return ResponseEntity.status(403).body(Map.of("message", "You are not authorized to delete this subject"));
        }
        
        // Thanks to `ON DELETE CASCADE` in our database.sql,
        // all enrollments for this subject will be automatically
        // deleted by the database.
        subjectRepository.delete(subject);
        return ResponseEntity.ok(Map.of("message", "Subject deleted successfully"));
    }
}
