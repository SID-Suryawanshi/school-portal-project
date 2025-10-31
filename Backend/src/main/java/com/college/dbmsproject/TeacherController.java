package com.college.dbmsproject;

import com.college.dbmsproject.Enrollment.FinalResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- NEW ---
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    // --- END NEW ---


    // --- MANAGE STUDENTS (No change) ---
    @GetMapping("/students")
    public ResponseEntity<List<ProfileResponseDTO>> getAllStudents(@RequestParam(value = "q", required = false) String query) {
        List<Profile> students;
        if (query != null && !query.isEmpty()) {
            students = profileRepository.searchAllStudents(query);
        } else {
            students = profileRepository.findAllStudents();
        }
        List<ProfileResponseDTO> studentDTOs = students.stream()
                .map(ProfileResponseDTO::new)
                .toList();
        return ResponseEntity.ok(studentDTOs);
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody SignupRequest studentRequest) {
        if (userRepository.existsByEmail(studentRequest.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Email is already in use!"));
        }
        User user = new User();
        user.setEmail(studentRequest.getEmail());
        user.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        user.setRole(Role.STUDENT);
        User savedUser = userRepository.save(user);
        Profile profile = new Profile(
                savedUser, studentRequest.getFirstName(), studentRequest.getLastName(),
                studentRequest.getAge(), studentRequest.getMobile(), studentRequest.getAddress(),
                studentRequest.getParentMobile()
        );
        profileRepository.save(profile);
        return ResponseEntity.ok(Map.of("message", "Student created successfully!"));
    }

    @DeleteMapping("/students/{userId}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        if (user.getRole() != Role.STUDENT) {
            return ResponseEntity.status(403).body(Map.of("message", "Cannot delete non-student users"));
        }
        userRepository.delete(user);
        return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));
    }

    // --- *** NEW GRADEBOOK ENDPOINTS *** ---

    /**
     * 1. GET ALL SUBJECTS FOR THE LOGGED-IN TEACHER
     */
    @GetMapping("/my-subjects")
    public ResponseEntity<List<Subject>> getMySubjects(@AuthenticationPrincipal UserDetails userDetails) {
        // Find the logged-in teacher's profile
        Profile teacherProfile = getTeacherProfile(userDetails);
        
        // Find subjects associated with this profile
        List<Subject> subjects = subjectRepository.findByTeacherProfile(teacherProfile);
        return ResponseEntity.ok(subjects);
    }

    /**
     * 2. GET ALL STUDENTS ENROLLED IN ONE OF THE TEACHER'S SUBJECTS
     */
    @GetMapping("/subject/{subjectId}/enrollments")
    public ResponseEntity<List<EnrollmentGradeDTO>> getEnrollmentsForSubject(
            @PathVariable Integer subjectId, 
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Profile teacherProfile = getTeacherProfile(userDetails);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Security check: Is this teacher allowed to grade this subject?
        if (!subject.getTeacherProfile().getId().equals(teacherProfile.getId())) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        // Find all enrollments for this subject
        List<Enrollment> enrollments = enrollmentRepository.findBySubjectWithStudentDetails(subject);

        // Convert to DTOs
        List<EnrollmentGradeDTO> dtos = enrollments.stream()
                .map(EnrollmentGradeDTO::new)
                .toList();
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * 3. UPDATE A STUDENT'S GRADE FOR AN ENROLLMENT
     */
    @PutMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<?> updateGrade(
            @PathVariable Integer enrollmentId, 
            @RequestBody GradeUpdateRequest gradeRequest, 
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Profile teacherProfile = getTeacherProfile(userDetails);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        // Security check: Make sure this enrollment is for a subject taught by this teacher
        if (!enrollment.getSubject().getTeacherProfile().getId().equals(teacherProfile.getId())) {
            return ResponseEntity.status(403).body(Map.of("message", "You are not authorized to grade this enrollment"));
        }

        // Update the enrollment
        enrollment.setMarks(gradeRequest.getMarks());
        enrollment.setGrade(gradeRequest.getGrade());
        enrollment.setFinalResult(gradeRequest.getFinalResult());
        
        enrollmentRepository.save(enrollment);

        return ResponseEntity.ok(Map.of("message", "Grade updated successfully"));
    }

    // Helper method to get the teacher's profile
    private Profile getTeacherProfile(UserDetails userDetails) {
        User teacherUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return profileRepository.findByUser(teacherUser)
                .orElseThrow(() -> new RuntimeException("Profile not found for user"));
    }
}