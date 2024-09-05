package com.project.shopapp.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    // class for configuration security

    // This is some endpoint authorize pass system
    private final String[] PUBLIC_ENDPOINTS = {
            "/authenticate", "/introspect", "/login"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests((request) ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated());

//        httpSecurity.oauth2ResourceServer(oauth2 ->
//
//        )

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
}
