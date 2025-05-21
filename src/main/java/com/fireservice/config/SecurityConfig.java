package com.fireservice.config;

import com.fireservice.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Основные настройки безопасности HTTP запросов
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        // Разрешаем доступ к этим URL без авторизации
                        .requestMatchers("/", "/home", "/register", "/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                        // Все остальные URL требуют авторизации
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")               // Своя страница логина
                        .defaultSuccessUrl("/home")        // После успешного логина редирект на /home
                        .permitAll()                       // Страница логина доступна всем
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/home")         // После логаута редирект на /home
                        .permitAll()                       // Логаут доступен всем
                );

        return http.build();
    }

    // Кодировщик паролей (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
