package com.badr.secspring.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;


public class JwtService {
    @Value("${jwt.secre}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;
    private SecretKey getSignedKey(){
        byte[] keyBytes=
                Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateToken (UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSignedKey())
                .compact();
    }
    private Claims extractAllclaims(String token){
        return Jwts.parser()
                .verifyWith(getSignedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String extractUsername(String token){
        return extractAllclaims(token).getSubject();
    }
    private boolean isTokenExpired(String token){
        final Date expiration = extractAllclaims(token).getExpiration();
        return expiration.before(new Date());
    }
    public boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
