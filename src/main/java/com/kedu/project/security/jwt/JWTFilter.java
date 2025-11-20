package com.kedu.project.security.jwt;

import java.io.IOException;
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
        String header = request.getHeader("Authorization");

        // 해더 확인 후 해더가 없거나 bearer가 아니라면 처리를 안해주고 보내줌(나중에 뒤에서 걸림)
        if (header == null || !header.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        

        String token = header.substring(7);
        try {
            if (jwt.validateToken(token)) {
                String id = jwt.getIdFromToken(token);
                // List<Integer> babySeqList = jwt.getRolesFromToken(token);

                // List<SimpleGrantedAuthority> auths = new ArrayList<>(); // 권한 목록 저장용 리스트
                // for (Integer babySeq : babySeqList) {
                // }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(id, null);
                        // auths);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                // S.C.H에 인증 및 인가 정보 저장
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            // 안정성을 위한것
        }
        filterChain.doFilter(request, response);
    }
}
