package com.cristian.gestor_tareas.security;


import com.cristian.gestor_tareas.model.User;
import com.cristian.gestor_tareas.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio personalizado que implementa la interfaz de Spring Security {@link UserDetailsService}.
 * Se encarga de buscar las credenciales del usuario en la base de datos y adaptarlas
 * al formato requerido por el contexto de seguridad.
 * * @author Cristian
 * @version 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor para la inyección del repositorio de usuarios.
     *
     * @param userRepository Repositorio para la consulta de datos de usuario.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga los datos de un usuario a partir de su nombre de usuario.
     *
     * @param username El nombre de usuario que intenta autenticarse.
     * @return Un objeto {@link UserDetails} con las credenciales y roles asignados válidos para Spring Security.
     * @throws UsernameNotFoundException Si el nombre de usuario no existe en la base de datos de Neon.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.get().getUsername())
                .password(user.get().getPassword())
                .authorities("USER")
                .build();
    }
}