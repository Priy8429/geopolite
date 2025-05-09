package com.priyhotel.config;

import com.priyhotel.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/auth/**").permitAll()
//                                .requestMatchers("/h2-console/**").permitAll()
//                                .requestMatchers("/api/v1/bookings/request").permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        .requestMatchers("/api/v1/admin/**").authenticated()// Admin-only
//                        .requestMatchers("/api/v1/user/**").authenticated() // User-only
//                        .requestMatchers(HttpMethod.GET, "/api/v1/rooms/**").permitAll() // Public access to view rooms
//                        .requestMatchers(HttpMethod.GET, "/api/v1/locations").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/hotels/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/v1/room-types/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/hotels/location/{location}").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/hotels").authenticated() // Only Admins can add rooms
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/hotels/**").authenticated() // Only Admins can update rooms
//                        .requestMatchers(HttpMethod.DELETE, "/api/v1/hotels/**").authenticated()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/rooms/**").authenticated() // Only Admins can add rooms
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/rooms/**").authenticated() // Only Admins can update rooms
//                        .requestMatchers(HttpMethod.DELETE, "/api/v1/rooms/**").authenticated() // Only Admins can delete rooms
//                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings").authenticated() // Only logged-in users can book
//                        .requestMatchers(HttpMethod.GET, "/api/v1/bookings/user/**").authenticated() // Users can view their own bookings
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/bookings/{id}/cancel").authenticated() // Users can cancel their bookings
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/locations/**").authenticated()
//                                .requestMatchers("/api/v1/**").permitAll()
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("*")); // React frontend origin
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // allow cookies or Authorization headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
