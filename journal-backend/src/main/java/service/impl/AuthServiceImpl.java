package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

/**
 * Implemented AuthService
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final RoleRepository        roleRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      jwtTokenProvider;

    private static final String         DEFAULT_ADMIN_USERNAME = "sahitiT";
    private static final String         DEFAULT_STAFF_USERNAME = "ST";

    // private static final String DEFAULT_ADMIN_PASSWORD = "csc326**";

    // List<User> getAllUsers();
    // List<User> getAllCustomers();
    // List<User> getAllStaff();

    @Override
    public List<User> getAllUsers () {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllCustomers () {
        final Role customerRole = roleRepository.findByName( "ROLE_CUSTOMER" );
        return userRepository.findByRolesContaining( customerRole );
    }

    @Override
    public List<User> getAllStaff () {
        final Role staffRole = roleRepository.findByName( "ROLE_STAFF" );
        return userRepository.findByRolesContaining( staffRole );
    }

    @Override
    public List<User> getAllMarketers () {
        final Role marketerRole = roleRepository.findByName( "ROLE_MARKETER" );
        return userRepository.findByRolesContaining( marketerRole );
    }

    /**
     * @author Diya Bhavsar
     */
    @Override
    public String addStaff ( final String name, final String username, final String email ) {

        // checking for username duplicates
        if ( userRepository.existsByUsername( username ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }

        // checking for email duplicates
        if ( userRepository.existsByEmail( email ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User staff = new User();
        staff.setName( name );
        staff.setUsername( username );
        staff.setEmail( email );
        // staff.setPassword(null);

        // temp password
        staff.setPassword( passwordEncoder.encode( "temporaryPassword123" ) );

        final Set<Role> roles = new HashSet<>();
        final Role staffRole = roleRepository.findByName( "ROLE_STAFF" );
        roles.add( staffRole );
        staff.setRoles( roles );

        userRepository.save( staff );

        return "Staff member added successfully. They can now complete their registration.";
    }

    @Override
    public String addMarketer ( final String name, final String username, final String email ) {

        // checking for username duplicates
        if ( userRepository.existsByUsername( username ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }

        // checking for email duplicates
        if ( userRepository.existsByEmail( email ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User marketer = new User();
        marketer.setName( name );
        marketer.setUsername( username );
        marketer.setEmail( email );
        // marketer.setPassword(null);

        // temp password
        marketer.setPassword( passwordEncoder.encode( "temporaryPassword123" ) );

        final Set<Role> roles = new HashSet<>();
        final Role marketerRole = roleRepository.findByName( "ROLE_MARKETER" );
        roles.add( marketerRole );
        marketer.setRoles( roles );

        userRepository.save( marketer );

        return "Marketer added successfully. They can now complete their registration.";
    }

    /**
     * @author Diya Bhavsar
     */
    @Override
    public String editUser ( final String existingUsername, final String newUsername, final String newName,
            final String newEmail, final String newPassword ) {
        // Find the user by the provided username
        final User user = userRepository.findByUsername( existingUsername ).orElseThrow(
                () -> new ResourceNotFoundException( "User not found with username " + existingUsername ) );

        // Check and update the username if provided
        if ( newUsername != null && !newUsername.isEmpty() && !newUsername.equals( existingUsername ) ) {
            // Check if the new username is already in use
            if ( userRepository.existsByUsername( newUsername ) ) {
                throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
            }
            user.setUsername( newUsername );
        }

        // Check and update the email if provided
        if ( newEmail != null && !newEmail.isEmpty() ) {
            if ( !newEmail.equals( user.getEmail() ) ) {
                // Check if the new email is already in use by another user
                if ( userRepository.existsByEmail( newEmail ) ) {
                    throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
                }
                user.setEmail( newEmail );
            }
        }

        // Check and update the name if provided
        if ( newName != null && !newName.isEmpty() ) {
            user.setName( newName );
        }

        // Check and update the password if provided
        if ( newPassword != null && !newPassword.isEmpty() ) {
            user.setPassword( passwordEncoder.encode( newPassword ) );
        }

        // Save the updated user
        userRepository.save( user );

        return "User details have been updated successfully.";
    }

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    @Override
    public String register ( final RegisterDto registerDto ) {

        // HERE CHECK IF THE USERNAME OR EMAIL IS UNDER STAFF ROLE
        final Optional<User> existingStaff = userRepository.findByUsernameOrEmail( registerDto.getUsername(),
                registerDto.getEmail() );

        if ( existingStaff.isPresent() ) {
            // Complete the registration for STAFF
            final User user = existingStaff.get();
            // if (!user.getPassword().isEmpty()) {
            // throw new WolfCafeAPIException(HttpStatus.BAD_REQUEST, "User is
            // already registered.");
            // }

            user.setName( registerDto.getName() );
            user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );
            userRepository.save( user );
            return "Staff registration completed successfully.";
        }
        // STAFF ENDS

        // CHECKS FOR CUSTOMER
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User user = new User();
        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( "ROLE_CUSTOMER" );
        roles.add( userRole );
        user.setRoles( roles );
        userRepository.save( user );

        return "User registered successfully.";
    }

    @Override
    public JwtAuthResponse login ( final LoginDto loginDto ) {
        System.out.println( "Authenticating user with username/email: " + loginDto.getUsernameOrEmail() );

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDto.getUsernameOrEmail(), loginDto.getPassword() ) );
        System.out.println( "Authentication successful for user: " + loginDto.getUsernameOrEmail() );

        SecurityContextHolder.getContext().setAuthentication( authentication );

        final String token = jwtTokenProvider.generateToken( authentication );
        System.out.println( "Generated JWT token: " + token );

        final Optional<User> userOptional = userRepository.findByUsernameOrEmail( loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail() );

        String role = "ROLE_CUSTOMER";

        if ( userOptional.isPresent() ) {
            final User loggedInUser = userOptional.get();
            System.out.println( "User found: " + loggedInUser.getUsername() );

            if ( loggedInUser.getUsername().equals( DEFAULT_ADMIN_USERNAME ) ) {
                System.out.println( "User is admin, assigning ROLE_ADMIN" );

                // Assign "ROLE_ADMIN" to "sahitiT"
                role = "ROLE_ADMIN";
                final Set<Role> roles = new HashSet<>();
                final Role adminRole = roleRepository.findByName( "ROLE_ADMIN" );
                roles.add( adminRole );
                loggedInUser.setRoles( roles );
                userRepository.save( loggedInUser );
            }
            else if ( loggedInUser.getUsername().equals( DEFAULT_STAFF_USERNAME ) ) {
                System.out.println( "User is staff, assigning ROLE_STAFF" );

                // Assign "ROLE_ADMIN" to "sahitiT"
                role = "ROLE_STAFF";
                final Set<Role> roles = new HashSet<>();
                final Role staffRole = roleRepository.findByName( "ROLE_STAFF" );
                roles.add( staffRole );
                loggedInUser.setRoles( roles );
                userRepository.save( loggedInUser );

            }
            else {
                System.out.println( "User not found with username/email: " + loginDto.getUsernameOrEmail() );

                role = loggedInUser.getRoles().stream().map( Role::getName ).findFirst().orElse( "ROLE_CUSTOMER" );
            }
        }

        final JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole( role );
        jwtAuthResponse.setAccessToken( token );

        return jwtAuthResponse;
    }

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     *
     * @author Diya Bhavsar
     */
    @Override
    @Transactional
    public void deleteUserByUsername ( final String username ) {
        final User user = userRepository.findByUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with username " + username ) );

        // Removing associations
        user.getRoles().clear();
        userRepository.save( user );

        // deleting the user
        userRepository.deleteUserByUsername( username );
    }

    /**
     * Checks if the currently authenticated user has ADMIN or STAFF roles.
     *
     * @return true if the user is an admin or staff, false otherwise
     */
    @Override
    public boolean isUserAdminOrStaff () {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null && authentication.getPrincipal() instanceof UserDetails ) {
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getAuthorities().stream()
                    .anyMatch( grantedAuthority -> grantedAuthority.getAuthority().equals( "ROLE_ADMIN" )
                            || grantedAuthority.getAuthority().equals( "ROLE_STAFF" ) );
        }
        return false;
    }

    @Override
    public boolean isUserAdmin () {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null && authentication.getPrincipal() instanceof UserDetails ) {
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getAuthorities().stream()
                    .anyMatch( grantedAuthority -> grantedAuthority.getAuthority().equals( "ROLE_ADMIN" ) );
        }
        return false;
    }

}
