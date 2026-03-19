package com.taskapp.task_service.config;

import com.taskapp.task_service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1 + 2: Get and check header
        String header = request.getHeader("Authorization");
        log.debug("Authorization header: {}", header); // ← add this
        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("No Bearer token found"); // ← add this
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract token
        String token = header.substring(7);
        log.debug("Token extracted: {}", token); // ← add this

        try {
            // Step 4: Extract username
            String username = jwtService.extractUsername(token);
            log.debug("Username extracted: {}", username); // ← add this

            // Step 5: Only authenticate if not already authenticated
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user from DB
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);
                log.debug("User loaded: {}", userDetails.getUsername()); // ← add this

                // Validate token
                if (jwtService.validateToken(token)) {
                    log.debug("Token is valid, setting authentication");

                    // Step 6: Create auth object and set in SecurityContext
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("JWT Filter error: {}", e.getMessage()); // ← add this!
            // Any token error → just continue without authenticating
            // Spring Security will return 401 for protected endpoints
            SecurityContextHolder.clearContext();
        }

        // Step 7: Always continue the chain
        filterChain.doFilter(request, response);
    }
}
