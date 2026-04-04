package com.cristian.gestor_tareas.controller;

import com.cristian.gestor_tareas.dto.LoginRequest;
import com.cristian.gestor_tareas.model.User;
import com.cristian.gestor_tareas.security.JwtUtil;
import com.cristian.gestor_tareas.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setRole("USER"); // ← AÑADIMOS ESTE PASO PARA ASIGNAR UN ROL POR DEFECTO A LOS USUARIOS REGISTRADOS
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of("token", token));
    }

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
