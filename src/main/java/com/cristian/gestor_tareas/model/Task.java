package com.cristian.gestor_tareas.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad principal que representa una tarea dentro del tablero Kanban.
 * Almacena toda la información contextual del objetivo (prioridades, fechas límite, estados),
 * y mantiene relaciones con el usuario propietario y su lista de subtareas hijas.
 * * @author Cristian
 * @version 1.0
 */
@Data
@Entity
@Table(name = "tasks")
public class Task {

    /**
     * Identificador único autoincremental de la tarea.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre o título descriptivo de la tarea. No puede ser nulo.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Detalle extendido de los requerimientos de la tarea. Soporta hasta 1000 caracteres.
     */
    @Column(length = 1000)
    private String description;

    /**
     * Estado actual de la tarea dentro del flujo de trabajo (ej. PENDING, IN_PROGRESS, DONE).
     */
    @Column(nullable = false)
    private String status; // PENDING, IN_PROGRESS, DONE

    /**
     * Índice numérico de posición. Utilizado para persistir el orden exacto de las tarjetas
     * cuando el usuario realiza operaciones de arrastrar y soltar (drag-and-drop).
     */
    private Integer orderIndex; // Para mantener el orden de las tareas

    /**
     * Nivel de urgencia de la tarea (HIGH, MEDIUM, LOW). Por defecto está establecida en "MEDIUM".
     */
    @Column(nullable = false)
    private String priority = "MEDIUM"; // ALTA, MEDIA, BAJA

    /**
     * Fecha límite establecida por el usuario para la finalización de la tarea.
     */
    private LocalDate deadline;
    /**
     * Sello de tiempo que registra el momento exacto en el que la tarea fue dada de alta.
     */
    private LocalDateTime createdAt;

    /**
     * Etiqueta o categoría opcional para agrupar y filtrar tareas de distintas temáticas.
     */
    @Column(nullable = true)
    private String category;

    /**
     * Usuario propietario de la tarea. Establece una relación de muchos a uno obligatoria.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Listado de subtareas asociadas de forma dependiente a esta tarea.
     * Aplica borrado en cascada y eliminación de huérfanos para que si la tarea principal se elimina,
     * todas sus subtareas se limpien automáticamente de la base de datos.
     * Utiliza {@link JsonManagedReference} como contraparte del control de recursividad JSON.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Subtask> subtasks = new ArrayList<>();

    /**
     * Método de ciclo de vida JPA que se ejecuta de forma automática antes de persistir
     * el registro, asignando la fecha y hora actuales a la propiedad {@code createdAt}.
     */
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}

