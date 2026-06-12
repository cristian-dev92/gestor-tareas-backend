package com.cristian.gestor_tareas.service;

import com.cristian.gestor_tareas.dto.TaskOrderUpdateDTO;
import com.cristian.gestor_tareas.model.Task;
import com.cristian.gestor_tareas.model.User;
import com.cristian.gestor_tareas.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar la lógica de negocio de las tareas en el sistema.
 * Modula las operaciones de ordenación en el tablero, la creación con valores por defecto,
 * actualizaciones de estado y filtrados por propiedad del usuario.
 * * @author Cristian
 * @version 1.0
 */
@Service
public class TaskService {
    private final TaskRepository taskRepository;

    /**
     * Constructor para la inyección de dependencias del repositorio de tareas.
     *
     * @param taskRepository Repositorio para interactuar con la persistencia de tareas.
     */
    public TaskService(TaskRepository taskRepository) {

        this.taskRepository = taskRepository;
    }

    /**
     * Reordena de forma masiva los índices de posición de un listado de tareas.
     * Convierte el DTO entrante en un mapa asociativo para indexar eficientemente las entidades
     * recuperadas y realiza una única actualización por lotes en la base de datos.
     *
     * @param updates Listado de objetos {@link TaskOrderUpdateDTO} que contienen los ID y sus nuevas posiciones.
     */
    public void reorderTasks(List<TaskOrderUpdateDTO> updates) {
        // Transformamos la lista a un mapa [ID -> orderIndex] para agilizar la asignación
        Map<Long, Integer> map = updates.stream()
                .collect(Collectors.toMap(TaskOrderUpdateDTO::getId, TaskOrderUpdateDTO::getOrderIndex));
        // Recuperamos todas las tareas involucradas de una sola vez
        List<Task> tasks = taskRepository.findAllById(map.keySet());
        // Seteamos las nuevas posiciones
        for (Task task : tasks) {
            Integer newOrder = map.get(task.getId());
            task.setOrderIndex(newOrder);
        }
        // Persistimos todas las actualizaciones en bloque
        taskRepository.saveAll(tasks);
    }

    /**
     * Recupera todas las tareas pertenecientes a un usuario concreto basándose en su ID.
     *
     * @param userId Identificador único del usuario propietario.
     * @return Una lista de objetos {@link Task} asociados al usuario.
     */
    public List<Task> getTasksForUser(Long userId) {

        return taskRepository.findByUserId(userId);
    }

    /**
     * Crea una nueva tarea en el sistema vinculándola a un usuario y asegurando
     * que cuente con valores válidos por defecto para su estado, prioridad y marca de tiempo.
     *
     * @param task Entidad {@link Task} en bruto enviada desde el cliente.
     * @param user Entidad {@link User} que será asignada como propietaria de la tarea.
     * @return La tarea persistida con sus campos por defecto e ID inicializados.
     */
    public Task createTask(Task task, User user) {
        task.setUser(user);

        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }

        if (task.getPriority() == null) {
            task.setPriority("MEDIUM");
        }
        // Registro del timestamp de creación
        task.setCreatedAt(java.time.LocalDateTime.now());

        return taskRepository.save(task);
    }

    /**
     * Actualiza y consolida de forma directa los cambios de una tarea existente en el almacenamiento.
     *
     * @param task Entidad {@link Task} modificada a persistir.
     * @return La tarea actualizada tras el proceso de sincronización.
     */
    public Task updateTask(Task task) {

        return taskRepository.save(task);
    }

    /**
     * Elimina de manera definitiva una tarea del sistema mediante su clave primaria.
     *
     * @param id Identificador único (ID) de la tarea a suprimir.
     */
    public void deleteTask(Long id) {

        taskRepository.deleteById(id);
    }

    /**
     * Filtra y obtiene una lista de tareas del sistema en base a su estado de flujo de trabajo.
     *
     * @param status Cadena de texto que representa el estado a buscar (ej. PENDING, DONE).
     * @return Listado de objetos {@link Task} que coinciden con el criterio solicitado.
     */
    public List<Task> getTasksByStatus(String status) {

        return taskRepository.findByStatus(status);
    }

    /**
     * Busca una tarea específica en el repositorio mediante su identificador.
     *
     * @param id Identificador único de la tarea.
     * @return Un contenedor {@link Optional} que incluye la {@link Task} si es localizada, o vacío si no existe.
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

}


