package com.cristian.gestor_tareas.service;

import com.cristian.gestor_tareas.model.User;
import com.cristian.gestor_tareas.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de orquestar la lógica de negocio asociada a las cuentas de usuario.
 * Proporciona métodos para el registro seguro aplicando hashing de contraseñas mediante BCrypt,
 * verificación criptográfica durante el proceso de login y actualización de perfiles de usuario.
 * * @author Cristian
 * @version 1.0
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor para la inyección de dependencias de persistencia y criptografía de contraseñas.
     *
     * @param userRepository Repositorio para la persistencia de usuarios.
     * @param passwordEncoder Componente de encriptación hash BCrypt.
     */
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Localiza a un usuario en el sistema a partir de su nombre de identidad único.
     *
     * @param username Nombre de usuario a buscar.
     * @return El objeto {@link User} correspondiente si es encontrado, o {@code null} en caso contrario.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Registra un nuevo usuario aplicando un algoritmo de cifrado irreversible (hashing)
     * sobre la contraseña en texto plano para garantizar los estándares de seguridad.
     *
     * @param user Entidad {@link User} con las credenciales originales aportadas en el formulario.
     * @return El usuario guardado con su contraseña debidamente encriptada.
     */
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Valida las credenciales de un usuario que intenta acceder al sistema.
     * Realiza un cotejo criptográfico seguro entre la contraseña en texto plano aportada y el hash guardado.
     *
     * @param username Nombre de usuario que solicita la autenticación.
     * @param password Contraseña en texto plano a verificar.
     * @return La entidad {@link User} completa si el login es exitoso, o {@code null} si las credenciales son inválidas.
     */
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Contrastamos el texto plano con el hash almacenado mediante BCrypt
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;

    }

    /**
     * Guarda o actualiza la información modificada de un perfil de usuario.
     * Cuenta con una validación preventiva para evitar el doble encriptado accidental de una clave
     * que ya se encuentra en formato hash.
     *
     * @param user Entidad {@link User} con los campos actualizados.
     * @return El usuario consolidado y guardado en la base de datos de Neon.
     */
    public User save(User user) {
        // Si la contraseña NO está encriptada, la encriptamos
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Localiza a un usuario a través de su dirección de correo electrónico única.
     *
     * @param email Dirección de correo para la consulta.
     * @return Un {@link Optional} que contiene el usuario si existe.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Localiza a un usuario basándose estrictamente en su clave primaria secuencial.
     *
     * @param id Identificador único (ID) del usuario.
     * @return Un {@link Optional} con los datos del usuario si es localizado.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

}

