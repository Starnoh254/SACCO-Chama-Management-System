package com.starnoh.sacco_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF if you are building a stateless REST API (using JWTs)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configure endpoint authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow anyone to access the registration endpoint
                        .requestMatchers("/api/v1/auth/register").permitAll()
                        // All other requests still require authentication
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
