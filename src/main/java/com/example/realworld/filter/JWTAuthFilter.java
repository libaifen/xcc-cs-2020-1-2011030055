package com.example.realworld.filter;

import com.example.realworld.utils.JwtUtils;
import com.example.realworld.provider.AuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends GenericFilter {
    public static final String TOKEN_PREFIX = "Token ";
    private final JwtUtils jwtUtils;
    private final AuthenticationProvider authenticationProvider;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        Optional.ofNullable(((HttpServletRequest) request).getHeader(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(TOKEN_PREFIX))
                .map(authHeader -> authHeader.substring(TOKEN_PREFIX.length()))
                .filter(jwtUtils::validateToken)
                .map(jwtUtils::getSub)
                .map(authenticationProvider::getAuthentication)
                .ifPresent(SecurityContextHolder.getContext()::setAuthentication);
        chain.doFilter(request, response);
    }
}