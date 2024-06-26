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
                                                .requestMatchers(HttpMethod.GET, "/uploads/*/**", "/auth/token",
                                                                "/auth/confirm/resend",
                                                                "/users/user/confirm_email",
                                                                "/messages/{senderId}/{recipientId}",
                                                                "/messages/{userId}")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/users", "users/admin")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/posts", "/posts/user/{userId}",
                                                                "/posts/post/{postId}", "/posts/{region}",
                                                                "/messages/block")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.POST, "/auth/register/admin")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/posts/post",
                                                                "/comments/comment/{postId}",
                                                                "/comments/sub_comment/{postId}", "/messages/block")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/login-dash",
                                                                "/auth/register",
                                                                "/auth/confirm", "/chat")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.PATCH, "/users/user/send_code",
                                                                "/users/user/change_password",
                                                                "/messages/{senderId}/{roomId}")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.PATCH, "/users/user").hasRole("USER")
                                                .requestMatchers(HttpMethod.PATCH, "/users/{userId}",
                                                                "/users/admin/{userId}",
                                                                "/users/user/block/{userId}")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PATCH, "/users/{userId}/image")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.DELETE, "/posts/post/{userId}",
                                                                "/comments/sub_comment/{commentId}",
                                                                "/comments/comment/{commentId}")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.DELETE, "/messages/{senderId}/{messageId}")
                                                .permitAll()
                                                .requestMatchers("/wss").permitAll()
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
