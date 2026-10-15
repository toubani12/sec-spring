package com.badr.secspring;

import com.badr.secspring.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTests {

    private JwtService service(long expiration) {
        JwtService s = new JwtService();
        ReflectionTestUtils.setField(s, "secret", "lZ+ebiWo34+BED7vEQTyFWPuVKJalLA9Lkn8jTL1Eg8=");
        ReflectionTestUtils.setField(s, "expiration", expiration);
        return s;
    }

    private final UserDetails alice = User.withUsername("alice@enset.ma").password("x").roles("USER").build();
    private final UserDetails bob = User.withUsername("bob@enset.ma").password("x").roles("USER").build();

    @Test
    void generatedTokenContainsSubjectAndIsValid() {
        JwtService s = service(60_000);
        String token = s.generateToken(alice);
        assertEquals(3, token.split("\\.").length);
        assertEquals("alice@enset.ma", s.extractUsername(token));
        assertTrue(s.isTokenValid(token, alice));
    }

    @Test
    void tokenOfAnotherUserIsRejected() {
        JwtService s = service(60_000);
        assertFalse(s.isTokenValid(s.generateToken(alice), bob));
    }

    @Test
    void expiredTokenThrows() {
        JwtService s = service(-1_000);
        String token = s.generateToken(alice);
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> s.isTokenValid(token, alice));
    }
}
