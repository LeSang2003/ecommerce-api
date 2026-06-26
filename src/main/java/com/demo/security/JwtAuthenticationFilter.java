package com.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
   
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
) throws ServletException, IOException {

    String path = request.getServletPath();

    // Bỏ qua API auth
    if (path.startsWith("/api/auth")) {
        filterChain.doFilter(request, response);
        return;
    }

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    String jwt = authHeader.substring(7);
    String username = null;

    try {
        username = jwtService.extractUsername(jwt);
    } catch (Exception e) {
        filterChain.doFilter(request, response);
        return;
    }

    if (username != null) {

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    //CHECK BAN(không phụ thuộc authentication)
    if (userDetails instanceof com.demo.model.User user) {
        if (user.getRole() != com.demo.model.Role.ADMIN 
            && Boolean.TRUE.equals(user.getBanned())) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("User is banned");
            return;
        }
    }

    if (SecurityContextHolder.getContext().getAuthentication() == null
            && jwtService.isTokenValid(jwt, userDetails)) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    filterChain.doFilter(request, response);
}
}    
