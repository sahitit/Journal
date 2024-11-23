package edu.ncsu.csc326.wolfcafe.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Provides a token for the user.
 */
@Component
public class JwtTokenProvider {

    /** Pulls secret from application.properties */
    @Value ( "${app.jwt-secret}" )
    private String jwtSecret;

    /** Pulls experiation of user login from application.properties */
    @Value ( "${app.jwt-expiration-milliseconds}" )
    private Long   jwtExpirationDate;

    /**
     * Generates the token
     *
     * @param authentication
     *            authentication object
     * @return the generated token
     */
    public String generateToken ( final Authentication authentication ) {
        final String username = authentication.getName();

        final Date currentDate = new Date();
        final Date expireDate = new Date( currentDate.getTime() + jwtExpirationDate );

        return Jwts.builder().subject( username ).issuedAt( currentDate ).expiration( expireDate ).signWith( key() )
                .compact();
    }

    private SecretKey key () {
        return Keys.hmacShaKeyFor( Decoders.BASE64.decode( jwtSecret ) );
    }

    /**
     * Returns the username
     *
     * @param token
     *            token to use for authentication
     * @return the username that is authenticated
     */
    public String getUsername ( final String token ) {
        final Claims claims = Jwts.parser().verifyWith( key() ).build().parseSignedClaims( token ).getPayload();

        return claims.getSubject(); // username

    }

    /**
     * Checks the token is valid.
     *
     * @param token
     *            token to check
     * @return true if valid
     */
    public boolean validateToken ( final String token ) {
        Jwts.parser().verifyWith( key() ).build().parse( token );
        return true;
    }
}
