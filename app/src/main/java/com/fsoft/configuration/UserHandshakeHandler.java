package com.fsoft.configuration;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        String userId = getUserIdFromRequest(request);
        return () -> userId; // returns a Principal with getName() = userId
    }

    private String getUserIdFromRequest(ServerHttpRequest request) {
        // From query string: /ws?userId=42
        URI uri = request.getURI();
        String query = uri.getQuery(); // e.g. "userId=42"
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("userId=")) {
                    return param.substring("userId=".length());
                }
            }
        }
        throw new IllegalArgumentException("Missing userId in request");
    }
}
