// JWTFilter.java
package com.kedu.project.security.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwt;

   @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getRequestURI();
    String method = request.getMethod();
    String header = request.getHeader("Authorization");

    // JWT가 존재하면 인증 정보 세팅
    if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
        try {
            if (jwt.validateToken(token)) {
                String id = jwt.getIdFromToken(token);
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(id, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
    }

    // GET /board/** 등 인증 없이 접근 가능한 요청
    boolean permitWithoutAuth = (method.equals("GET") && path.startsWith("/board"))
            || path.startsWith("/ws-stomp")
            || path.startsWith("/sockjs")
            || path.startsWith("/user/idChack")
            || path.startsWith("/user/nicknameChack")
            || path.startsWith("/user/signup")
            || path.startsWith("/user/login")
            || path.startsWith("/user/pindIdByEmail")
            || path.startsWith("/user/pindPwByEmail")
            || path.startsWith("/file/")
            || path.startsWith("/emailCheck")
            || path.startsWith("/emailCheck/");

    if (permitWithoutAuth) {
        filterChain.doFilter(request, response);
        return;
    }

    // POST 등 인증이 반드시 필요한 요청
    if (!permitWithoutAuth && (header == null || !header.startsWith("Bearer "))) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing or invalid");
        return;
    }

    filterChain.doFilter(request, response);
}
}
