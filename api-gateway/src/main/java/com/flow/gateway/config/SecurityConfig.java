package com.flow.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(authorize -> authorize.anyRequest()
                .authenticated())
                .oauth2ResourceServer(oauth2->oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
