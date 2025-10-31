package com.college.dbmsproject;

import com.college.dbmsproject.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
// We don't need @CrossOrigin here anymore, it's handled in SecurityConfig
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken(authentication);

            User user = (User) authentication.getPrincipal();
            
            // Return token, email, and role
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("email", user.getUsername()); // email is the username
            response.put("role", user.getRole().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error: Invalid email or password");
        }
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }

        // Determine role based on if any users exist
        Role userRole;
        if (userRepository.count() == 0) {
            userRole = Role.TEACHER; // First user is always TEACHER (admin)
        } else {
            userRole = Role.STUDENT; // All other users are STUDENTS
        }

        // Create new user's account
        User user = new User(
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                userRole // Assign the role we just determined
        );

        User savedUser = userRepository.save(user);

        // Create new profile associated with the user
        Profile profile = new Profile(
                savedUser,
                signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getAge(),
                signupRequest.getMobile(),
                signupRequest.getAddress(),
                signupRequest.getParentMobile()
        );

        // This is the FIXED code
        profileRepository.save(profile);

        // FIX: Send back a JSON object instead of a plain string
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }
}