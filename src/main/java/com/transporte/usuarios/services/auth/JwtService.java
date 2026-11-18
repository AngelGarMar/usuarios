package com.transporte.usuarios.services.auth;

import com.transporte.usuarios.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${app.maintenance.expirationInMinutes}")
    private Long EXPIRATION_IN_MINUTES;

    @Value("${app.maintenance.secretKey}")
    private String SECRE_KEY;

    public String generateToken(User user, Map<String, Object> extraClaims) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date((EXPIRATION_IN_MINUTES * 60 * 1000) + issuedAt.getTime()); //en milisegundos
        String jwt = Jwts.builder()
                .header()
                    .type("JWT")
                .and()
                .claims(extraClaims)
                .subject(user.getNombre())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(generateKey(), Jwts.SIG.HS256)
                .compact();
        return jwt;
    }

    private SecretKey generateKey() {
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRE_KEY);
        return Keys.hmacShaKeyFor(passwordDecoded);
    }
}
