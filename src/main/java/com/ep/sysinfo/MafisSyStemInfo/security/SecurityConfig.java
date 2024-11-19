package com.ep.sysinfo.MafisSyStemInfo.security;

import com.ep.sysinfo.MafisSyStemInfo.model.Benutzer;
import com.ep.sysinfo.MafisSyStemInfo.repository.BenutzerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final BenutzerRepository benutzerRepository;

    // UserDetailsService Bean to retrieve user details
    @Bean
    UserDetailsService userDetailsService() {
        return username -> {
            Benutzer benutzer = Optional.ofNullable(benutzerRepository.findByUsername(username))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return benutzer;
        };
    }

    // Password encoder Bean (BCrypt for both API and form login security)
    @Bean
    public BCryptPasswordEncoder  passwordEncoder() {
        return new BCryptPasswordEncoder();  // Using BCryptPasswordEncoder for both API and form login
    }

    // AuthenticationProvider setup using DaoAuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // AuthenticationManager Bean to handle authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // API security configuration (Basic Authentication for /rest/**)
    @Bean
    @Order(1)  // Order is important to differentiate between multiple configurations
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for API
                .securityMatcher("/rest/**")  // Use securityMatcher to apply this configuration to /rest/** paths
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/rest/**").permitAll()  // Allow everyone to access /rest/** paths
                )
                .httpBasic(Customizer.withDefaults());  // Use httpBasic(Customizer.withDefaults())
        return http.build();
    }

    // Form login security configuration
    @Bean
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/resources/**", "/static/**","/rest/**", "/css/**", "/js/**", "/images/**","/webjars/**", "/h2-console/**","/addUser").permitAll()  // Allow public access to static resources and H2 console
                        .anyRequest().hasAnyAuthority("user", "admin")  // Allow both "user" and "admin" roles for other requests
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Custom login page
                        .defaultSuccessUrl("/homepage", true)  // Redirect to home on successful login
                        .permitAll()  // Allow everyone to access the login page
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutUrl("/logout")  // Custom logout URL
                        .deleteCookies("JSESSIONID")  // Delete session cookie on logout
                        .invalidateHttpSession(true)  // Invalidate session
                        .clearAuthentication(true)  // Clear authentication on logout
                )
                .csrf(AbstractHttpConfigurer::disable);  // Disable CSRF for simplicity
        return http.build();
    }
}
