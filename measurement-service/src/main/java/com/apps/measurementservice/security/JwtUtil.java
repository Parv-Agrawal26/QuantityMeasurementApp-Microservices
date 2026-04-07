package com.apps.measurementservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret:MySuperSecretKeyForJwt}")
    private String secret;

    public boolean validateToken(String token) {
        try {
            getVerifier().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = getVerifier().verify(token);
        return jwt.getSubject();
    }

    public String getRoleFromToken(String token) {
        DecodedJWT jwt = getVerifier().verify(token);
        return jwt.getClaim("role").asString();
    }

    private JWTVerifier getVerifier() {
        return JWT.require(Algorithm.HMAC256(secret)).build();
    }
}
