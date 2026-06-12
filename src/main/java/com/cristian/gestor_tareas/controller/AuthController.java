package com.cristian.gestor_tareas.controller;

import com.cristian.gestor_tareas.dto.LoginRequest;
import com.cristian.gestor_tareas.model.User;
import com.cristian.gestor_tareas.security.JwtUtil;
import com.cristian.gestor_tareas.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST encargado de gestionar los endpoints de autenticación y usuarios.
 * Proporciona rutas para el registro, inicio de sesión y gestión del perfil del usuario autenticado.
 * * @author Cristian
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Registra un nuevo usuario en el sistema asignándole un rol por defecto.
     *
     * @param userService Objeto {@link User} con los datos del registro (username, password, email).
     * @return {@link ResponseEntity} con el usuario registrado en el cuerpo de la respuesta y estado 200 OK.
     */
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registra un nuevo usuario en el sistema asignándole un rol por defecto.
     *
     * @param user Objeto {@link User} con los datos del registro (username, password, email).
     * @return {@link ResponseEntity} con el usuario registrado en el cuerpo de la respuesta y estado 200 OK.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setRole("USER"); // ← AÑADIMOS ESTE PASO PARA ASIGNAR UN ROL POR DEFECTO A LOS USUARIOS REGISTRADOS
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Autentica a un usuario mediante sus credenciales y genera un token de acceso JWT.
     *
     * @param loginRequest Objeto DTO {@link LoginRequest} que contiene el username y password.
     * @return {@link ResponseEntity} con un mapa que contiene el "token" generado si las credenciales son válidas (200 OK),
     * o un mensaje de error con estado 401 Unauthorized si son inválidas.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * Obtiene el perfil del usuario actualmente autenticado basándose en el token JWT de la cabecera.
     *
     * @param request Objeto {@link HttpServletRequest} utilizado para extraer la cabecera 'Authorization'.
     * @return {@link ResponseEntity} con el objeto {@link User} (sin contraseña por seguridad) y estado 200 OK,
     * estado 401 Unauthorized si falta el token, o 404 Not Found si el usuario no existe.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
}

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        user.setPassword(null); // muy importante: no devolver la contraseña

        return ResponseEntity.ok(user);
    }

    /**
     * Modifica los datos del perfil del usuario autenticado (username, email, password)
     * y genera un nuevo token JWT adaptado a los posibles cambios.
     *
     * @param updatedUser Objeto {@link User} con los nuevos datos a actualizar.
     * @param request Objeto {@link HttpServletRequest} para extraer el token JWT actual y validar la identidad.
     * @return {@link ResponseEntity} con un mapa que contiene el "user" actualizado (sin password) y el "token" nuevo si la operación tiene éxito (200 OK),
     * estado 401 Unauthorized si el token es inválido, o 404 Not Found si el usuario no existe en el sistema.
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser, HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Actualizamos los campos permitidos
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword()); // tu servicio ya la encripta
        }

        User saved = userService.save(user);

        saved.setPassword(null); // nunca devolver contraseña

        String newToken = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of(
                "user", saved,
                "token", newToken
        ));

    }
}
