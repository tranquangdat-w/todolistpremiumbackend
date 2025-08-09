package com.fsoft.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fsoft.dto.UserDto;

@Component
public class JwtTokenManager {

  public String generateToken(UserDto user, long timeToExpire, String secretKey, String issuer) {
    return JWT.create()
        .withIssuer(issuer)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + timeToExpire * 60 * 1000))
        .withClaim("id", user.getId().toString())
        .withClaim("name", user.getName())
        .withClaim("username", user.getUsername())
        .withClaim("email", user.getEmail())
        .withClaim("userRole", user.getUserRole().name())
        .withClaim("isActive", user.isActive())
        .sign(Algorithm.HMAC256(secretKey));
  }

  public DecodedJWT validateToken(String token, String secretKey) {
    final JWTVerifier jwtVerifier = JWT
        .require(Algorithm.HMAC256(secretKey))
        .build();

    return jwtVerifier.verify(token);
  }
}
