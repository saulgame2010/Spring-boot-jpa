package com.example.springboot.data.jpa.app.auth.filter;

import com.example.springboot.data.jpa.app.auth.SimpleGrantedAuthorityMixin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    public static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (!requiresAuthentication(header)) {
            chain.doFilter(request, response);
            return;
        }

        boolean tokenValid;
        Claims token = null;

        try {
            token = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJwt(header.replace("Bearer ", "")).getBody();
            tokenValid = true;
        } catch (JwtException | IllegalArgumentException e) {
            tokenValid = false;
        }

        UsernamePasswordAuthenticationToken authenticationToken = null;

        if (tokenValid) {
            String username = token.getSubject();
            Object roles = token.get("authorities");
            Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper()
                    .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
                    .readValue(roles.toString().getBytes(),
                            SimpleGrantedAuthority[].class));
            authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    protected boolean requiresAuthentication(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return false;
        }
        return true;
    }
}
