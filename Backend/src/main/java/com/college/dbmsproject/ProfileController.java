package com.college.dbmsproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    // Get the profile of the currently logged-in user
    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDTO> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = profileRepository.findByUser(user) 
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        // FIX: Return the DTO, not the Profile entity
        return ResponseEntity.ok(new ProfileResponseDTO(profile));
    }

    // Update the profile of the currently logged-in user
    @PutMapping("/me")
    public ResponseEntity<ProfileResponseDTO> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Profile profileDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFirstName(profileDetails.getFirstName());
        profile.setLastName(profileDetails.getLastName());
        profile.setAge(profileDetails.getAge());
        profile.setMobile(profileDetails.getMobile());
        profile.setAddress(profileDetails.getAddress());

        if (user.getRole() == Role.STUDENT) {
            profile.setParentMobile(profileDetails.getParentMobile());
        }

        Profile updatedProfile = profileRepository.save(profile);
        
        // FIX: Return the DTO, not the Profile entity
        return ResponseEntity.ok(new ProfileResponseDTO(updatedProfile));
    }
}