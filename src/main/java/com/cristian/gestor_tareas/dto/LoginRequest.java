package com.cristian.gestor_tareas.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
