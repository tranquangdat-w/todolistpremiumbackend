package com.fsoft.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fsoft.model.User;
import com.fsoft.model.UserRole;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtTokenManager {
  public String genrateToken(User user, long timeToExpire, String secretKey, String issuer) {
    final String username = user.getUsername();
    final UserRole userRole = user.getUserRole();

    return JWT.create()
        .withIssuer(issuer)
        .withClaim("role", userRole.name())
        .withClaim("username", username)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + timeToExpire * 60 * 1000))
        .withIssuer(secretKey)
        .sign(Algorithm.HMAC256(secretKey));
  }

  public DecodedJWT validateToken(String token, String secretKey) {
    final JWTVerifier jwtVerifier = JWT
        .require(Algorithm.HMAC256(secretKey))
        .build();

    return jwtVerifier.verify(token);
  }
}
