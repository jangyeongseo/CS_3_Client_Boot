package com.kedu.project.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JWTUtil {
	@Value("${jwt.expiration}")
	private Long exp;

	private Algorithm algorithm;
	private JWTVerifier jwt;
	private String secret;

	public JWTUtil(@Value("${jwt.secret}") String secret) {
		this.secret = secret;
		this.algorithm = Algorithm.HMAC256(secret);
		this.jwt = JWT.require(algorithm).build();
	}

	public String createToken(String id) {
		return JWT.create()
				.withSubject(id)
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt(new Date(System.currentTimeMillis() + exp))
				.sign(this.algorithm);

	}

	public DecodedJWT verifyToken(String token) {
		return jwt.verify(token);
	}

	public boolean validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).build();
			verifier.verify(token);
			return true;
		} catch (JWTVerificationException e) {
			return false;
		}
	}

	public String getIdFromToken(String token) {
		DecodedJWT data = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
		return data.getSubject();
	}

}
