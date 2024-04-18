package com.projeto.interdisciplinar.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.projeto.interdisciplinar.services.SecurityFilter;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
        @Autowired
        SecurityFilter securityFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authorize -> authorize
                                                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR)
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/uploads/users/**", "/auth/token",
                                                                "/auth/confirm/resend",
                                                                "/users/user/confirm_email")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/users", "users/admin")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/posts", "/posts/post/{userId}")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.POST, "/auth/register/admin")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/posts/post",
                                                                "/comments/comment/{postId}",
                                                                "/comments/sub_comment/{postId}")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register",
                                                                "/auth/confirm")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.PATCH, "/users/user/send_code",
                                                                "/users/user/change_password")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.PATCH, "/users/user").hasRole("USER")
                                                .requestMatchers(HttpMethod.PATCH, "/users/{userId}",
                                                                "/users/user/block/{userId}")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PATCH, "/users/{userId}/image")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.DELETE, "/posts/post/{userId}",
                                                                "/comments/sub_comment/{commentId}",
                                                                "/comments/comment/{commentId}")
                                                .hasRole("USER")
                                                .anyRequest().denyAll())
                                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
