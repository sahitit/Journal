package edu.ncsu.csc326.wolfcafe.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilterTest {

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

    @BeforeEach
    public void setUp () {
        MockitoAnnotations.openMocks( this );
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternal_ValidToken () throws ServletException, IOException {
        final String token = "validToken";
        final String username = "testUser";

        when( request.getHeader( "Authorization" ) ).thenReturn( "Bearer " + token );
        when( jwtTokenProvider.validateToken( token ) ).thenReturn( true );
        when( jwtTokenProvider.getUsername( token ) ).thenReturn( username );

        final UserDetails userDetails = mock( UserDetails.class );
        when( userDetailsService.loadUserByUsername( username ) ).thenReturn( userDetails );

        jwtAuthenticationFilter.doFilterInternal( request, response, filterChain );

        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        assertNotNull( authentication );
        assertEquals( userDetails, authentication.getPrincipal() );

        verify( filterChain, times( 1 ) ).doFilter( request, response );
    }

    @Test
    public void testDoFilterInternal_InvalidToken () throws ServletException, IOException {
        final String token = "invalidToken";

        when( request.getHeader( "Authorization" ) ).thenReturn( "Bearer " + token );
        when( jwtTokenProvider.validateToken( token ) ).thenReturn( false );

        jwtAuthenticationFilter.doFilterInternal( request, response, filterChain );

        assertNull( SecurityContextHolder.getContext().getAuthentication() );
        verify( filterChain, times( 1 ) ).doFilter( request, response );
    }

    @Test
    public void testDoFilterInternal_NoToken () throws ServletException, IOException {
        when( request.getHeader( "Authorization" ) ).thenReturn( null );

        jwtAuthenticationFilter.doFilterInternal( request, response, filterChain );

        assertNull( SecurityContextHolder.getContext().getAuthentication() );
        verify( filterChain, times( 1 ) ).doFilter( request, response );
    }

    // @Test
    // public void testGetTokenFromRequest_WithBearerPrefix () {
    // when( request.getHeader( "Authorization" ) ).thenReturn( "Bearer
    // validToken" );
    //
    // final String token = jwtAuthenticationFilter.getTokenFromRequest( request
    // );
    // assertEquals( "validToken", token );
    // }
    //
    // @Test
    // public void testGetTokenFromRequest_NoBearerPrefix () {
    // when( request.getHeader( "Authorization" ) ).thenReturn( "validToken" );
    //
    // final String token = jwtAuthenticationFilter.getTokenFromRequest( request
    // );
    // assertNull( token ); // Should return null because it does not start
    // // with "Bearer "
    // }
}
