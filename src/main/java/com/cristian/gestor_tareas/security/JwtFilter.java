package com.cristian.gestor_tareas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de interceptación que se ejecuta exactamente una vez por cada petición HTTP entrante.
 * Su función principal es extraer el token JWT de la cabecera 'Authorization', validarlo,
 * y registrar al usuario en el contexto de seguridad de Spring si el token es legítimo.
 * * @author Cristian
 * @version 1.0
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor para inyectar las utilidades de JWT y el cargador de detalles de usuario.
     *
     * @param jwtUtil Utility para descifrar y validar el token.
     * @param userDetailsService Servicio para cargar el usuario desde persistencia.
     */
    public JwtFilter(JwtUtil jwtUtil,  UserDetailsService userDetailsService) {

        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Lógica interna del filtro que procesa la petición para aplicar la seguridad JWT.
     *
     * @param request El objeto de la petición HTTP.
     * @param response El objeto de la respuesta HTTP.
     * @param filterChain La cadena de filtros de Spring Security a la que se cederá el control.
     * @throws ServletException Si ocurre un error en el procesamiento del servlet.
     * @throws IOException Si ocurre un error de entrada/salida durante la intercepción.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                              @NonNull HttpServletResponse response,
                                              @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);//Cargamos usuario final desde la base de datos

                if (jwtUtil.validateToken(token, userDetails)) { //validamos el token

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); //creamos autenticación válida

                    authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request) );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
