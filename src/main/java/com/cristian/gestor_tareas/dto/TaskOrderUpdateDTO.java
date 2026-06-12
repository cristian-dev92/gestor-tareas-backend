package com.cristian.gestor_tareas.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) diseñado para el procesamiento y sincronización de posiciones.
 * Captura de forma optimizada el identificador de la tarea y su nuevo índice de ordenación
 * cuando el usuario interactúa con el arrastrar y soltar (drag-and-drop) en el tablero Kanban.
 * * @author Cristian
 * @version 1.0
 */
@Getter
@Setter
public class TaskOrderUpdateDTO {
    /**
     * Identificador único (ID) de la tarea que ha sido reordenada o movida de posición.
     */
    private Long id;
    /**
     * El nuevo índice numérico de posición secuencial que ocupará la tarjeta dentro de su columna.
     */
    private Integer orderIndex;

}
