package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Controller for authentication functionality.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/auth" )
@AllArgsConstructor
public class AuthController {

    /** Link to AuthService */
    private final AuthService authService;

    /**
     * Adds a new staff member's name, username, and email. Requires the ADMIN
     * role.
     *
     * @param staffRegistrationDto
     *            object with staff registration info
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PostMapping ( "/addStaff" )
    public ResponseEntity<String> addStaff ( @RequestBody final RegisterDto staffRegistrationDto ) {
        final String response = authService.addStaff( staffRegistrationDto.getName(),
                staffRegistrationDto.getUsername(), staffRegistrationDto.getEmail() );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PostMapping ( "/addMarketer" )
    public ResponseEntity<String> addMarketer ( @RequestBody final RegisterDto marketerRegistrationDto ) {
        final String response = authService.addMarketer( marketerRegistrationDto.getName(),
                marketerRegistrationDto.getUsername(), marketerRegistrationDto.getEmail() );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Edits the details of the logged-in user (name, username, email,
     * password).
     *
     * @param username
     *            the username of the user to be updated
     * @param name
     *            new name of the user
     * @param email
     *            new email of the user
     * @param password
     *            new password of the user
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PutMapping ( "/{username}" )
    public ResponseEntity<String> editUser ( @PathVariable final String username,
            @RequestBody final Map<String, String> updateFields ) {

        final String newUsername = updateFields.get( "username" );
        final String name = updateFields.get( "name" );
        final String email = updateFields.get( "email" );
        final String password = updateFields.get( "password" );

        // Calling the editUser method from AuthService to perform the update
        final String response = authService.editUser( username, newUsername, name, email, password );

        return new ResponseEntity<>( response, HttpStatus.OK );
    }

    /**
     * Gets all the users of the system
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @GetMapping ( "/users" )
    public ResponseEntity<List<User>> getAllUsers () {
        final List<User> users = authService.getAllUsers();
        return ResponseEntity.ok( users );
    }

    /**
     * Gets all the customers of the system
     *
     * @return all customers
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @GetMapping ( "/customers" )
    public ResponseEntity<List<User>> getAllCustomers () {
        final List<User> customers = authService.getAllCustomers();
        return ResponseEntity.ok( customers );
    }

    /**
     * Gets all the staff users of the system
     *
     * @return staff users
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @GetMapping ( "/staff" )
    public ResponseEntity<List<User>> getAllStaff () {
        final List<User> staffMembers = authService.getAllStaff();
        return ResponseEntity.ok( staffMembers );
    }

    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @GetMapping ( "/marketers" )
    public ResponseEntity<List<User>> getAllMarketers () {
        final List<User> marketers = authService.getAllMarketers();
        return ResponseEntity.ok( marketers );
    }

    /**
     * Registers a new customer user with the system.
     *
     * @param registerDto
     *            object with registration info
     * @return response indicating success or failure
     */
    @PostMapping ( "/register" )
    public ResponseEntity<String> register ( @RequestBody final RegisterDto registerDto ) {
        final String response = authService.register( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Logs in the given user
     *
     * @param loginDto
     *            user information for login
     * @return object representing the logged in user
     */
    @PostMapping ( "/login" )
    public ResponseEntity<JwtAuthResponse> login ( @RequestBody final LoginDto loginDto ) {
        System.out.println( "Received login request for username/email: " + loginDto.getUsernameOrEmail() );

        final JwtAuthResponse jwtAuthResponse = authService.login( loginDto );
        System.out.println( "Returning JWT token and role: " + jwtAuthResponse.getAccessToken() + ", "
                + jwtAuthResponse.getRole() );

        return new ResponseEntity<>( jwtAuthResponse, HttpStatus.OK );
    }

    /**
     * Deletes the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( "/users/{username}" )
    public ResponseEntity<String> deleteUser ( @PathVariable ( "username" ) final String username ) {
        authService.deleteUserByUsername( username );
        return ResponseEntity.ok( "User deleted successfully." );
    }

    /**
     * Checks if the logged-in user is a staff member or admin.
     *
     * @return response indicating if the user is staff or admin
     */
    @GetMapping ( "/isAdminOrStaff" )
    public ResponseEntity<String> isAdminOrStaff () {
        final boolean isAuthorized = authService.isUserAdminOrStaff();

        if ( isAuthorized ) {
            return ResponseEntity.ok( "User is authorized as staff or admin." );
        }
        else {
            return new ResponseEntity<>( "User is not authorized.", HttpStatus.FORBIDDEN );
        }
    }

}
