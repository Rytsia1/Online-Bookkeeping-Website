package com.DTMK.Online.Bookkeeping.Website.Project.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // Kunci rahasia untuk menandatangani token (minimal 256-bit)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Masa aktif token: 24 Jam
    private final long expiration = 86400000;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }
}