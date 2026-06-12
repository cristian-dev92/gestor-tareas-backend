package com.cristian.gestor_tareas.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

/**
 * Clase de configuración principal de Spring Security.
 * Centraliza la desactivación de CSRF, establece la política de sesiones Stateless,
 * configura el mapeo dinámico de orígenes CORS a través de variables de entorno,
 * y define los privilegios de acceso público o privado para los endpoints de la API.
 * * @author Cristian
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Inyección dinámica de los orígenes autorizados para la comunicación CORS.
     * Los datos se recuperan desde las variables de entorno de IntelliJ/Render.
     */
    private final JwtFilter jwtFilter;

    @Value("${CORS_ALLOWED_ORIGINS}")
    private String corsAllowedOrigins;

    /**
     * Constructor para la inyección del filtro de intercepción JWT personalizado.
     *
     * @param jwtFilter Filtro encargado de la verificación de firmas JWT.
     */
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Bean encargado de proporcionar el algoritmo de encriptación hash BCrypt
     * para asegurar el almacenamiento de contraseñas de usuario.
     *
     * @return Instancia de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el comportamiento de seguridad global de la aplicación web (Filtros, CORS, Rutas).
     *
     * @param http Componente {@link HttpSecurity} utilizado para orquestar la configuración de accesos.
     * @return El objeto {@link SecurityFilterChain} compilado y configurado de forma estricta.
     * @throws Exception Si ocurre algún fallo de configuración durante el arranque del contexto.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .cors(cors -> cors.configurationSource(request -> {
            var corsConfig = new org.springframework.web.cors.CorsConfiguration();

            // CORS_ALLOWED_ORIGINS es una variable de entorno que contiene los orígenes permitidos separados por comas
            corsConfig.setAllowedOriginPatterns(Arrays.asList(corsAllowedOrigins.split(",")));

            corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfig.setAllowedHeaders(java.util.List.of("*"));
            corsConfig.setExposedHeaders(java.util.List.of("Authorization"));
            corsConfig.setAllowCredentials(true);
            return corsConfig;
        }));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // ⭐ PERMITIR OPTIONS
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/register").permitAll()
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

  }