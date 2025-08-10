package com.fsoft.security.jwt;

import java.io.Console;
import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.springframework.web.util.WebUtils;
import org.springframework.util.AntPathMatcher;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private JwtTokenManager jwtTokenManager;

  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  private JwtProperties jwtProperties;

  private static final List<String> WHITELIST_ENDPOINT = List.of(
      "/users/register",
      "/users/login",
      "/users/verify",
      "/users/logout",
      "/v3/api-docs",
      "/users/refresh_token");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String path = request.getRequestURI();

    boolean isWhitelisted = WHITELIST_ENDPOINT.stream()
        .anyMatch(pattern -> pathMatcher.match(pattern, path));

    if (isWhitelisted) {
      filterChain.doFilter(request, response);
      return;
    }

    Cookie cookie = WebUtils.getCookie(request, "accessToken");

    // Neu khong co accesstoken thi gui ma loi 401 dang xuat luon
    if (cookie == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write("{\"message\": \"Please login again\"}");
      response.getWriter().flush();
      return;
    }

    String accessToken = cookie.getValue().trim();

    // Kiem tra access token
    try {
      String accessTokenSecretKey = jwtProperties.getAccessTokenSecretKey();
      DecodedJWT decodedJWT = jwtTokenManager.validateToken(
          accessToken,
          accessTokenSecretKey);

      String username = decodedJWT.getClaim("username").asString();

      String role = decodedJWT.getClaim("userRole").asString();

      List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

      UserDetails userDetails = new User(username, "", authorities);

      Authentication authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);
    } catch (TokenExpiredException tokenExpiredException) {
      // Call api refresh token neu accesstoken het han
      response.setStatus(HttpServletResponse.SC_GONE);
      return;
    } catch (Exception exception) {
      // Nếu có bất kì lỗi nào khác ngoài lỗi hết hạn thì sẽ bị đăng xuất luôn
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write("{\"message\": \"Please login again\"}");
      response.getWriter().flush();
      return;
    }
  }
}
