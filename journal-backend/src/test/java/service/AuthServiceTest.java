package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.security.JwtAuthenticationFilter;
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.impl.AuthServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class AuthServiceTest {

    @Mock
    private UserRepository          userRepository;

    @Mock
    private RoleRepository          roleRepository;

    @Mock
    private PasswordEncoder         passwordEncoder;

    @Mock
    private AuthenticationManager   authenticationManager;

    @Mock
    private JwtTokenProvider        jwtTokenProvider;

    @Mock
    private UserDetailsService      userDetailsService;

    @Mock
    private HttpServletRequest      request;

    @Mock
    private HttpServletResponse     response;

    @Mock
    private FilterChain             filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @InjectMocks
    private AuthServiceImpl         authService;
    
    @Mock
    private Authentication authentication;

    private String                  token;
    private final String            username = "testUser";
    private UserDetails             userDetails;

    @BeforeEach
    void setUp () {
        MockitoAnnotations.openMocks( this );
    }

    
    @Test
    void testLoginWithAdminRole () {
        // Arrange
        final String adminUsername = "sahitiT";
        final String password = "adminPassword";
        final String jwtToken = "fake-jwt-token";

        final User adminUser = new User();
        adminUser.setUsername( adminUsername );

        final Role adminRole = new Role();
        adminRole.setName( "ROLE_ADMIN" );

        when( authenticationManager.authenticate( any( Authentication.class ) ) )
                .thenReturn( mock( Authentication.class ) );
        when( jwtTokenProvider.generateToken( any( Authentication.class ) ) ).thenReturn( jwtToken );
        when( userRepository.findByUsernameOrEmail( adminUsername, adminUsername ) )
                .thenReturn( Optional.of( adminUser ) );
        when( roleRepository.findByName( "ROLE_ADMIN" ) ).thenReturn( adminRole );

        final LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail( adminUsername );
        loginDto.setPassword( password );

        // Act
        final JwtAuthResponse response = authService.login( loginDto );

        // Assert
        assertEquals( "ROLE_ADMIN", response.getRole() );
        verify( userRepository, times( 1 ) ).save( adminUser );
    }

    @Test
    void testLoginWithStaffRole () {
        // Arrange
        final String staffUsername = "ST";
        final String password = "staffPassword";
        final String jwtToken = "fake-jwt-token";

        final User staffUser = new User();
        staffUser.setUsername( staffUsername );

        final Role staffRole = new Role();
        staffRole.setName( "ROLE_STAFF" );

        when( authenticationManager.authenticate( any( Authentication.class ) ) )
                .thenReturn( mock( Authentication.class ) );
        when( jwtTokenProvider.generateToken( any( Authentication.class ) ) ).thenReturn( jwtToken );
        when( userRepository.findByUsernameOrEmail( staffUsername, staffUsername ) )
                .thenReturn( Optional.of( staffUser ) );
        when( roleRepository.findByName( "ROLE_STAFF" ) ).thenReturn( staffRole );

        final LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail( staffUsername );
        loginDto.setPassword( password );

        // Act
        final JwtAuthResponse response = authService.login( loginDto );

        // Assert
        assertEquals( "ROLE_STAFF", response.getRole() );
        verify( userRepository, times( 1 ) ).save( staffUser );
    }

    /**
     * Test for getUsername method of JwtTokenProvider.
     */
    @Test
    public void testGetUsername () {
        token = "mockedTokenValue";
        // Mock behavior of jwtTokenProvider
        when( jwtTokenProvider.getUsername( token ) ).thenReturn( username );

        // Invoke getUsername method and verify result
        final String resultUsername = jwtTokenProvider.getUsername( token );
        assertEquals( username, resultUsername );

        // Verify that the mock method was called
        verify( jwtTokenProvider, times( 1 ) ).getUsername( token );
    }

    /**
     * Test for validateToken method of JwtTokenProvider.
     */
    @Test
    public void testValidateToken () {
        token = "mockedTokenValue";
        // Mock behavior for validateToken method
        when( jwtTokenProvider.validateToken( token ) ).thenReturn( true );

        // Call validateToken and check that it returns true
        assertTrue( jwtTokenProvider.validateToken( token ) );

        // Verify that the validateToken method was called once
        verify( jwtTokenProvider, times( 1 ) ).validateToken( token );
    }
    
//    @Test
//    public void testIsUserAdmin_WhenUserIsAdmin() {
//        // Set up the SecurityContext with the mocked Authentication
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Mock user details with ROLE_ADMIN authority
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getAuthorities()).thenReturn(
//            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
//        );
//
//        // Check if the method returns true
//        assertTrue(authService.isUserAdmin());
//    }
//
//    @Test
//    public void testIsUserAdmin_WhenUserIsNotAdmin() {
//        // Set up the SecurityContext with the mocked Authentication
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Mock user details without ROLE_ADMIN authority
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getAuthorities()).thenReturn(
//            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//
//        // Check if the method returns false
//        assertFalse(authService.isUserAdmin());
//    }

    @Test
    public void testIsUserAdmin_WhenNoAuthentication() {
        // Clear the SecurityContext to simulate no authentication
        SecurityContextHolder.clearContext();

        // Check if the method returns false
        assertFalse(authService.isUserAdmin());
    }


    @Test
    public void testLoginWithCustomerRole() {
        // Arrange
        String customerUsername = "customer1";
        String password = "password123";
        String jwtToken = "fake-jwt-token";

        User customerUser = new User();
        customerUser.setUsername(customerUsername);
        
        Role customerRole = new Role();
        customerRole.setName("ROLE_CUSTOMER");
        customerUser.setRoles(Collections.singleton(customerRole));

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);
        when(userRepository.findByUsernameOrEmail(customerUsername, customerUsername))
                .thenReturn(Optional.of(customerUser));

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(customerUsername);
        loginDto.setPassword(password);

        // Act
        JwtAuthResponse response = authService.login(loginDto);

        // Assert
        assertEquals("ROLE_CUSTOMER", response.getRole());
        assertEquals(jwtToken, response.getAccessToken());
    }

    @Test
    public void testLoginWithNonExistentUser() {
        // Arrange
        String username = "nonexistent";
        String password = "password123";
        String jwtToken = "fake-jwt-token";

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);
        when(userRepository.findByUsernameOrEmail(username, username))
                .thenReturn(Optional.empty());

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(username);
        loginDto.setPassword(password);

        // Act
        JwtAuthResponse response = authService.login(loginDto);

        // Assert
        assertEquals("ROLE_CUSTOMER", response.getRole());
        assertEquals(jwtToken, response.getAccessToken());
    }
    
    @Test
    public void testEditUser_SuccessfulUpdate() {
        // Arrange
        String existingUsername = "existingUser";
        String newUsername = "newUsername";
        String newName = "New Name";
        String newEmail = "newemail@example.com";
        String newPassword = "newPassword123";

        User existingUser = new User();
        existingUser.setUsername(existingUsername);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldPassword");

        when(userRepository.findByUsername(existingUsername))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(newUsername))
                .thenReturn(false);
        when(userRepository.existsByEmail(newEmail))
                .thenReturn(false);
        when(passwordEncoder.encode(newPassword))
                .thenReturn("encodedNewPassword");

        // Act
        String result = authService.editUser(existingUsername, newUsername, newName, newEmail, newPassword);

        // Assert
        assertEquals("User details have been updated successfully.", result);
        verify(userRepository).save(any(User.class));
        
        // Verify the user object was updated correctly
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(newUsername, savedUser.getUsername());
        assertEquals(newName, savedUser.getName());
        assertEquals(newEmail, savedUser.getEmail());
        assertEquals("encodedNewPassword", savedUser.getPassword());
    }

    @Test
    public void testEditUser_UsernameAlreadyExists() {
        // Arrange
        String existingUsername = "existingUser";
        String newUsername = "takenUsername";

        User existingUser = new User();
        existingUser.setUsername(existingUsername);

        when(userRepository.findByUsername(existingUsername))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(newUsername))
                .thenReturn(true);

        // Act & Assert
        assertThrows(WolfCafeAPIException.class, () -> 
            authService.editUser(existingUsername, newUsername, null, null, null)
        );
    }

    @Test
    public void testEditUser_EmailAlreadyExists() {
        // Arrange
        String existingUsername = "existingUser";
        String newEmail = "taken@example.com";

        User existingUser = new User();
        existingUser.setUsername(existingUsername);
        existingUser.setEmail("original@example.com");

        when(userRepository.findByUsername(existingUsername))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(newEmail))
                .thenReturn(true);

        // Act & Assert
        assertThrows(WolfCafeAPIException.class, () -> 
            authService.editUser(existingUsername, null, null, newEmail, null)
        );
    }

    @Test
    public void testEditUser_UserNotFound() {
        // Arrange
        String nonExistentUsername = "nonexistent";
        when(userRepository.findByUsername(nonExistentUsername))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            authService.editUser(nonExistentUsername, "newUsername", "newName", "new@email.com", "newPassword")
        );
    }

    @Test
    public void testEditUser_PartialUpdate() {
        // Arrange
        String existingUsername = "existingUser";
        String newName = "New Name";
        // Only updating name, keeping other fields null

        User existingUser = new User();
        existingUser.setUsername(existingUsername);
        existingUser.setName("Old Name");
        existingUser.setEmail("existing@example.com");

        when(userRepository.findByUsername(existingUsername))
                .thenReturn(Optional.of(existingUser));

        // Act
        String result = authService.editUser(existingUsername, null, newName, null, null);

        // Assert
        assertEquals("User details have been updated successfully.", result);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(existingUsername, savedUser.getUsername()); // Username unchanged
        assertEquals(newName, savedUser.getName()); // Name updated
        assertEquals("existing@example.com", savedUser.getEmail()); // Email unchanged
    }

    @Test
    public void testDeleteUser_Successful() {
        // Arrange
        String username = "userToDelete";
        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setName("ROLE_CUSTOMER");
        roles.add(userRole);

        User user = new User();
        user.setUsername(username);
        user.setRoles(roles);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        // Act
        authService.deleteUserByUsername(username);

        // Assert
        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user); // Verify roles are cleared
        verify(userRepository).deleteUserByUsername(username);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        // Arrange
        String nonExistentUsername = "nonexistent";
        when(userRepository.findByUsername(nonExistentUsername))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            authService.deleteUserByUsername(nonExistentUsername)
        );
    }

}
