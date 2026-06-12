package com.cristian.gestor_tareas.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa un usuario registrado dentro del sistema.
 * Contiene las credenciales de acceso, datos de contacto de carácter único y el rol asignado
 * para la gestión de políticas de seguridad e identidad en Spring Security.
 * * @author Cristian
 * @version 1.0
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /**
     * Identificador único autoincremental de la cuenta de usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único utilizado para el proceso de inicio de sesión. No puede repetirse.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Contraseña del usuario almacenada de forma segura (encriptada mediante algoritmo hash en capa service).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Dirección de correo electrónico única vinculada a la cuenta.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Rol asignado dentro del sistema (ej. USER, ADMIN) para la autorización y securización de endpoints.
     */
    @Column(nullable = false)
    private String role;
}
