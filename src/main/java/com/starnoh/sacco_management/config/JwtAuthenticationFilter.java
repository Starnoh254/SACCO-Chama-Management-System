package com.starnoh.sacco_management.config;

import com.starnoh.sacco_management.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Look for the "Authorization: Bearer <TOKEN>" header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token found? Pass it to the next filter (Spring will reject it if the endpoint is protected)
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the token string
        String jwt = authHeader.substring(7);

        try {
            // 4. Validate token signature and expiration
            if (jwtService.validateToken(jwt)) {
                // Extract userId as the unique identifier since email is missing/not used
                Long userId = jwtService.extractUserId(jwt);

                // 5. Check if user is not already authenticated in this request context
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 6. Extract role directly from the token payload (avoids DB hit)
                    String roleName = jwtService.extractRole(jwt);

                    // Spring Security expects roles to start with "ROLE_" prefix for standard role checks
                    String enforcedRole = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(enforcedRole));

                    // 7. Create Authentication token
                    // Pass the userId as the Principal (first argument) instead of email
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userId, // Principal is now the Long userId
                            null,   // Credentials are not needed anymore
                            authorities
                    );

                    // 8. Add request tracking details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 9. Authenticate the user for this session/request
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Clear context if any extraction error occurs
            SecurityContextHolder.clearContext();
        }


        // 5. Continue to your Controller endpoint
        filterChain.doFilter(request, response);
    }


}

