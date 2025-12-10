package com.kedu.project.comment.alarm;

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
        }

        if (token == null || !jwtUtil.validateToken(token)) {
            return false;
        }

        String userId = jwtUtil.getIdFromToken(token);
        StompPrincipal principal = new StompPrincipal(userId);
        attributes.put("principal", principal);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
