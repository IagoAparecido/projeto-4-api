package com.projeto.interdisciplinar.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.projeto.interdisciplinar.models.UsersModel;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String genarateToken(UsersModel userModel) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject((userModel.getEmail()))
                    .withExpiresAt(genExpirationDate())
                    .sign((algorithm));

            return token;

        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro na geração de token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException e) {
            return "";
        }
    }

    public Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(10000).toInstant(ZoneOffset.of("-04:00"));
    }
}
