package com.college.dbmsproject;

public class ProfileResponseDTO {

    // Fields from Profile
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String mobile;
    private String address;
    private String parentMobile;

    // --- THIS IS THE FIX ---
    // Instead of separate email/role, we have a nested UserDTO object
    private UserDTO user;
    // --- END OF FIX ---


    // Constructor that maps the Profile to this DTO
    public ProfileResponseDTO(Profile profile) {
        this.id = profile.getId();
        this.firstName = profile.getFirstName();
        this.lastName = profile.getLastName();
        this.age = profile.getAge();
        this.mobile = profile.getMobile();
        this.address = profile.getAddress();
        this.parentMobile = profile.getParentMobile();
        
        // This is the key fix:
        // We safely get the User object (which JOIN FETCH loaded)
        // and create a new UserDTO from it.
        if (profile.getUser() != null) {
            this.user = new UserDTO(profile.getUser());
        }
    }

    // Getters for all fields
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getParentMobile() {
        return parentMobile;
    }

    // Getter for the nested user object
    public UserDTO getUser() {
        return user;
    }
}