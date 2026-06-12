package com.cristian.gestor_tareas.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) estructurado para capturar las credenciales de acceso.
 * Se utiliza exclusivamente en el proceso de autenticación del endpoint de inicio de sesión.
 * * @author Cristian
 * @version 1.0
 */
@Data
public class LoginRequest {
    /**
     * El nombre de usuario o alias único de la cuenta que intenta iniciar sesión.
     */
    private String username;
    /**
     * La contraseña en texto plano enviada desde el formulario del Frontend para su posterior validación.
     */
    private String password;
}
