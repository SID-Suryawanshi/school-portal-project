package com.college.dbmsproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // This method is used by ProfileController
    // FIX: "JOIN FETCH p.user" tells Hibernate to also load the user data
    @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user = :user")
    Optional<Profile> findByUser(@Param("user") User user);

    // This method is used by TeacherController (getAllStudents)
    // FIX: "JOIN FETCH p.user" tells Hibernate to also load the user data
    @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user.role = 'STUDENT'")
    List<Profile> findAllStudents();

    // This method is used by TeacherController (search)
    // FIX: "JOIN FETCH p.user" tells Hibernate to also load the user data
    @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user.role = 'STUDENT' AND " +
           "(p.firstName LIKE %:query% OR p.lastName LIKE %:query% OR p.user.email LIKE %:query%)")
    List<Profile> searchAllStudents(@Param("query") String query);
}