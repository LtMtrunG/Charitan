package com.example.Chartian.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final CustomJWTDecoder jwtDecoder;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract JWT from cookies
        String jwt = getJwtFromCookies(request);

        if (jwt == null) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Access Token is missing");
            return;
        }
        try {
            // Decode and validate JWT
            var jwtDecoded = jwtDecoder.decode(jwt);
            System.out.println("JWT Subject: " + jwtDecoded.getSubject());

            // Load user details from the decoded JWT
            Long userId = Long.parseLong(jwtDecoded.getSubject());
            CustomUserDetails userDetails = userDetailsService.loadUserById(userId);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            System.out.println(userDetails.getAuthorities().toString());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
