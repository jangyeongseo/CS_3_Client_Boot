package com.kedu.project.security.jwt;

import java.util.Date;
import java.util.List;

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
	private Long exp; // 만료시간

	private Algorithm algorithm; // 무슨 암호화 쓸지 정하는 애
	private JWTVerifier jwt;
	private String secret;

	// 생성자
	public JWTUtil(@Value("${jwt.secret}") String secret) {
		this.secret = secret; // 필드에 저장
		this.algorithm = Algorithm.HMAC256(secret);
		this.jwt = JWT.require(algorithm).build();
	}

	public String createToken(String id, List<Integer> babySeqList) {
		return JWT.create()// 토큰생성하겟음()
				.withSubject(id)// 대표적으로쓸데이터()
				.withClaim("babySeqList", babySeqList)
				.withIssuedAt(new Date(System.currentTimeMillis())) // 토큰생성일(현재일자)
				.withExpiresAt(new Date(System.currentTimeMillis() + exp))// 토큰만료일(종료일)
				.sign(this.algorithm);// 이 알고리즘을 사용해서 암호값을 이음

	}

	// 토큰조작여부 확인 (인코딩 되어있던걸 디코딩 시켜서 리턴)
	public DecodedJWT verifyToken(String token) {
		return jwt.verify(token);
	}

	// 토큰 확인 (security 전용으로 true/ false 값 리턴)
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

	// 우리가 대표적으로 쓸데이터에 저장된걸 끄내기 (ex) id, email)
	public String getIdFromToken(String token) {
		DecodedJWT data = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
		return data.getSubject();
	}

	// withClaim 로 지정한 키값에 해당되는 value 값 뽑기 ( 현재 코드에선 roles로 권한 )
	public List<Integer> getRolesFromToken(String token) {
		DecodedJWT data = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
		return data.getClaim("babySeqList").asList(Integer.class);
	}    
}
