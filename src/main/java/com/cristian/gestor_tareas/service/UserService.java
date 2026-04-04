package com.cristian.gestor_tareas.service;

import com.cristian.gestor_tareas.model.User;
import com.cristian.gestor_tareas.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Inyección de dependencias por constructor
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        // Aquí podrías validar si el username o email ya existen
        // Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Comparar la contraseña encriptada con la proporcionada
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;

    }

    public User save(User user) {
        // Si la contraseña NO está encriptada, la encriptamos
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

}

