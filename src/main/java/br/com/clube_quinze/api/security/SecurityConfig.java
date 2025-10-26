package br.com.clube_quinze.api.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health", "/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                "/api/v1/auth/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return authorities -> {
            Set<GrantedAuthority> expanded = new HashSet<>();
            for (GrantedAuthority authority : authorities) {
                expanded.add(authority);
                expanded.addAll(expandAuthority(authority));
            }
            return List.copyOf(expanded);
        };
    }

    private Collection<? extends GrantedAuthority> expandAuthority(GrantedAuthority authority) {
        String role = authority.getAuthority();
        List<GrantedAuthority> result = new ArrayList<>();
        switch (role) {
            case "ROLE_CLUB_ADMIN" -> {
                result.add(new SimpleGrantedAuthority("ROLE_CLUB_EMPLOYE"));
                result.add(new SimpleGrantedAuthority("ROLE_CLUB_SELECT"));
                result.add(new SimpleGrantedAuthority("ROLE_CLUB_STANDARD"));
            }
            case "ROLE_CLUB_EMPLOYE" -> {
                result.add(new SimpleGrantedAuthority("ROLE_CLUB_SELECT"));
                result.add(new SimpleGrantedAuthority("ROLE_CLUB_STANDARD"));
            }
            case "ROLE_CLUB_SELECT" -> result.add(new SimpleGrantedAuthority("ROLE_CLUB_STANDARD"));
            default -> {
            }
        }
        return result;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
