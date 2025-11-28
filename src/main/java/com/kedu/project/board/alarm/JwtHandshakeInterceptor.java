package com.kedu.project.board.alarm;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.kedu.project.security.jwt.JWTUtil;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JWTUtil jwtUtil;

    public JwtHandshakeInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String token = null;

        if (request instanceof ServletServerHttpRequest req) {
            token = req.getServletRequest().getParameter("token");
            System.out.println("WebSocket 연결 시도, token: " + token);
            System.out.println("클라이언트 주소: " + req.getRemoteAddress());
        }

        if (token == null || !jwtUtil.validateToken(token)) {
            System.out.println("WebSocket 연결 실패: 유효하지 않은 토큰");
            return false;
        }

        String userId = jwtUtil.getIdFromToken(token);
        System.out.println("WebSocket 연결 성공, userId: " + userId);

        attributes.put("principal", new StompPrincipal(userId));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        System.out.println("afterHandshake 호출됨");
    }
}
