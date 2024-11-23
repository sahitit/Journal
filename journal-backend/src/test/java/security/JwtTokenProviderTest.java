package edu.ncsu.csc326.wolfcafe.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication   authentication;

    private final String     jwtSecret         = "YourJWTSecretKeyYourJWTSecretKeyYourJWTSecretKey";
    private final Long       jwtExpirationDate = 86400000L;                                         // 1
                                                                                                    // day
                                                                                                    // in
                                                                                                    // milliseconds

    @BeforeEach
    public void setUp () {
        MockitoAnnotations.openMocks( this );

        // Set the private fields in JwtTokenProvider
        ReflectionTestUtils.setField( jwtTokenProvider, "jwtSecret", jwtSecret );
        ReflectionTestUtils.setField( jwtTokenProvider, "jwtExpirationDate", jwtExpirationDate );
    }

    @Test
    public void testGetUsername () {
        when( authentication.getName() ).thenReturn( "testUser" );

        final String token = jwtTokenProvider.generateToken( authentication );
        final String username = jwtTokenProvider.getUsername( token );

        assertEquals( "testUser", username );
    }

    @Test
    public void testValidateToken () {
        when( authentication.getName() ).thenReturn( "testUser" );

        final String token = jwtTokenProvider.generateToken( authentication );
        final boolean isValid = jwtTokenProvider.validateToken( token );

        assertTrue( isValid );
    }

    @Test
    public void testExpiredToken () {
        // Override the expiration date to be in the past
        ReflectionTestUtils.setField( jwtTokenProvider, "jwtExpirationDate", -1000L );

        when( authentication.getName() ).thenReturn( "testUser" );

        final String expiredToken = jwtTokenProvider.generateToken( authentication );

        final Exception exception = assertThrows( Exception.class, () -> {
            jwtTokenProvider.validateToken( expiredToken );
        } );

        assertNotNull( exception );
    }
}
