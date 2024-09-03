package com.project.shopapp.filters;

import com.nimbusds.jose.util.Pair;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NotNull  HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (isBypassToken(request)) {
            filterChain.doFilter(request, response);
        }
        //filterChain.doFilter(request, response); // permit all

    }

    private boolean isBypassToken(@NotNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of("${api.prefix}/products", "GET"),
                Pair.of("${api.prefix}/categories", "GET"),
                Pair.of("${api.prefix}/users/register", "POST"),
                Pair.of("${api.prefix}/users/login", "POST")
        );
        for (Pair<String, String> bypassToken : bypassTokens) {
            if (request.getServletPath().contains(bypassToken.getLeft()) &&
                    request.getServletPath().contains(bypassToken.getRight())) {
                return true;
            }
        }
        return false;
    }
}
