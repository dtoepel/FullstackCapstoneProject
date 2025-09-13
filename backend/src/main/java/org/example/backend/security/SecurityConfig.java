package org.example.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.url}")
    private String appUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling(eh -> eh.authenticationEntryPoint((request, response, authException) -> {
            response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "OAuth realm=\"%s\"".formatted(appUrl));
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }));

        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/auth/me").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/election").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/election/candidates").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/election/email/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/election/results/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/election/result-analysis/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/election/result-condorcet/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/election/vote/**").permitAll()
                .anyRequest().authenticated()
            )
            .logout(o -> o.logoutSuccessUrl(appUrl))
            .oauth2Login(o -> o.defaultSuccessUrl(appUrl));
        return http.build();
    }

}
