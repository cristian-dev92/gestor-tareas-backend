package com.cristian.gestor_tareas.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa una subtarea concreta dentro del sistema.
 * Cada subtarea actúa como un elemento atómico de control (un "checklist")
 * que pertenece de forma obligatoria a una tarea padre.
 * * @author Cristian
 * @version 1.0
 */
@Getter
@Setter
@Entity
public class Subtask {

    /**
     * Identificador único autoincremental de la subtarea en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título o descripción corta de la acción a realizar en la subtarea.
     */
    private String title;

    /**
     * Estado de cumplimiento de la subtarea. Por defecto se inicializa en falso (no completada).
     */
    private boolean done = false;

    /**
     * Tarea padre a la que pertenece esta subtarea.
     * Utiliza {@link JsonBackReference} para omitir la serialización inversa en las peticiones HTTP
     * y evitar bucles infinitos de recursividad con la entidad {@link Task}.
     */
    @ManyToOne
    @JoinColumn(name = "task_id")
    @JsonBackReference
    private Task task;

}
