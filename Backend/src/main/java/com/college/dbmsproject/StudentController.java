package com.college.dbmsproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // 1. GET ALL SUBJECTS (for the "Select Subjects" page)
    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        // Students just need to read all available subjects
        return ResponseEntity.ok(subjectRepository.findAll());
    }

    // 2. ENROLL IN A SUBJECT
    @PostMapping("/enroll/{subjectId}")
    public ResponseEntity<?> enrollInSubject(@PathVariable Integer subjectId, @AuthenticationPrincipal UserDetails userDetails) {
        // Find the logged-in student
        User student = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Find the subject they want to enroll in
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Check if already enrolled
        if (enrollmentRepository.findByStudentAndSubject(student, subject).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "You are already enrolled in this subject"));
        }

        // Create the new enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(student);
        newEnrollment.setSubject(subject);
        // Grades/marks are null by default
        
        enrollmentRepository.save(newEnrollment);

        return ResponseEntity.ok(Map.of("message", "Successfully enrolled in " + subject.getTitle()));
    }

    // 3. GET MY RESULTS (for the "My Results" page)
    @GetMapping("/my-results")
    public ResponseEntity<List<EnrollmentDTO>> getMyResults(@AuthenticationPrincipal UserDetails userDetails) {
        // Find the logged-in student
        User student = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Find their enrollments using our new repository method
        List<Enrollment> enrollments = enrollmentRepository.findByStudentWithDetails(student);

        // Convert the list of entities to a list of DTOs
        List<EnrollmentDTO> dtos = enrollments.stream()
                .map(EnrollmentDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // 4. UN-ENROLL FROM A SUBJECT
    @DeleteMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<?> unenrollFromSubject(@PathVariable Integer enrollmentId, @AuthenticationPrincipal UserDetails userDetails) {
        // Find the logged-in student
        User student = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Find the enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        // Security Check: Make sure the enrollment belongs to the logged-in student
        if (!enrollment.getStudent().getId().equals(student.getId())) {
            return ResponseEntity.status(403).body(Map.of("message", "You are not authorized to remove this enrollment"));
        }

        enrollmentRepository.delete(enrollment);

        return ResponseEntity.ok(Map.of("message", "Successfully unenrolled"));
    }
}