package com.groommoa.aether_back_spring.global.auth.security;

import com.groommoa.aether_back_spring.global.auth.exception.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Getter
public class TokenExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e){
            response.sendError(e.getErrorCode().getHttpStatus().value(), e.getMessage());
        }
    }
}
