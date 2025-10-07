package com.example.config;

   import com.example.util.JwtUtil;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.security.config.annotation.web.builders.HttpSecurity;
   import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

       private final JwtUtil jwtUtil;
       private final JwtAuthenticationFilter jwtAuthenticationFilter;

       @Autowired
       public SecurityConfig(JwtUtil jwtUtil, JwtAuthenticationFilter jwtAuthenticationFilter) {
           this.jwtUtil = jwtUtil;
           this.jwtAuthenticationFilter = jwtAuthenticationFilter;
       }

       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http
                   .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                   .csrf(csrf -> csrf.disable())
                   .authorizeHttpRequests(auth -> auth
                           .requestMatchers(
                               "/api/auth/**",
                               "/v3/api-docs/**",
                               "/swagger-ui/**",
                               "/swagger-ui.html",
                               "/swagger-resources/**",
                               "/webjars/**"
                           ).permitAll()
                           .requestMatchers("/api/admin/**").hasRole("ADMIN")
                           .requestMatchers("/api/applications/**").hasRole("USER")
                           .anyRequest().authenticated()
                   )
                   .sessionManagement(session -> session
                           .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   )
                   .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

           return http.build();
       }

       @Bean
       public CorsConfigurationSource corsConfigurationSource() {
           CorsConfiguration config = new CorsConfiguration();
           config.setAllowedOrigins(List.of("http://localhost:3000"));
           config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
           config.setAllowedHeaders(List.of("*"));
           config.setAllowCredentials(true);
           UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
           source.registerCorsConfiguration("/**", config);
           return source;
       }

       @Bean
       public PasswordEncoder passwordEncoder() {
           return new BCryptPasswordEncoder();
       }
   }