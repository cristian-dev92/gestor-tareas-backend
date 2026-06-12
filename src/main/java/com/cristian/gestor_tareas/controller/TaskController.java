package com.cristian.gestor_tareas.controller;

import com.cristian.gestor_tareas.dto.TaskOrderUpdateDTO;
import com.cristian.gestor_tareas.model.Subtask;
import com.cristian.gestor_tareas.model.Task;
import com.cristian.gestor_tareas.model.User;
import com.cristian.gestor_tareas.repository.SubtaskRepository;
import com.cristian.gestor_tareas.repository.TaskRepository;
import com.cristian.gestor_tareas.service.TaskService;
import com.cristian.gestor_tareas.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar el ciclo de vida de las tareas y subtareas.
 * Proporciona endpoints para operaciones CRUD de tareas asociadas a usuarios autenticados,
 * control de estados (pending/done), gestión de subtareas y reordenación de posiciones en el tablero.
 * * @author Cristian
 * @version 1.0
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final SubtaskRepository subtaskRepository;

    /**
     * Constructor para la inyección de dependencias de servicios y repositorios.
     *
     * @param taskService Servicio para la lógica de negocio de tareas.
     * @param userService Servicio para la lógica de negocio de usuarios.
     * @param taskRepository Repositorio de persistencia para tareas.
     * @param subtaskRepository Repositorio de persistencia para subtareas.
     */
    public TaskController(TaskService taskService,
                          UserService userService,
                          TaskRepository taskRepository,
                          SubtaskRepository subtaskRepository)
    {
        this.taskService = taskService;
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.subtaskRepository = subtaskRepository;
    }

    /**
     * Obtiene el listado completo de tareas que pertenecen al usuario actualmente autenticado.
     *
     * @param request Objeto {@link HttpServletRequest} que provee el principal del usuario autenticado.
     * @return {@link ResponseEntity} con la lista de objetos {@link Task} vinculados al usuario y estado 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(taskService.getTasksForUser(user.getId()));
    }

    /**
     * Crea y guarda una nueva tarea vinculándola directamente al usuario autenticado.
     *
     * @param task Objeto {@link Task} enviado desde el cliente con los datos de la tarea.
     * @param request Objeto {@link HttpServletRequest} utilizado para identificar al dueño de la tarea.
     * @return {@link ResponseEntity} con la tarea guardada y su ID generado, con estado 200 OK.
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userService.findByUsername(username);
        Task saved = taskService.createTask(task, user);
        return ResponseEntity.ok(saved);
    }

    /**
     * Actualiza los detalles globales de una tarea existente basándose en su identificador único.
     *
     * @param id Identificador único (ID) de la tarea que se va a modificar.
     * @param task Objeto {@link Task} que contiene los nuevos valores modificados en el cliente.
     * @return {@link ResponseEntity} con el objeto {@link Task} actualizado en persistencia y estado 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return ResponseEntity.ok(taskService.updateTask(task));
    }

    /**
     * Elimina una tarea del sistema tras validar que pertenece de forma estricta al usuario autenticado.
     *
     * @param id Identificador único (ID) de la tarea que se pretende eliminar.
     * @param request Objeto {@link HttpServletRequest} para verificar la identidad del solicitante.
     * @return {@link ResponseEntity} confirmando la eliminación (200 OK), estado 403 Forbidden si el usuario
     * no es el propietario legítimo, o lanza una excepción en runtime si la tarea no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id,  HttpServletRequest request) {

        String username = request.getUserPrincipal().getName();
        User user = userService.findByUsername(username);

        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task == null || !task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You can't delete this task");
        }

        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted");
    }

    /**
     * Alterna de forma directa el estado de una tarea entre 'PENDING' y 'DONE'.
     * Valida que el usuario solicitante sea el propietario de la tarea antes de aplicar el cambio.
     *
     * @param id Identificador único (ID) de la tarea a conmutar.
     * @param request Objeto {@link HttpServletRequest} para comprobar la propiedad de la tarea.
     * @return {@link ResponseEntity} con la tarea modificada en su nuevo estado (200 OK),
     * o estado 403 Forbidden si la tarea pertenece a otra cuenta.
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleStatus(@PathVariable Long id, HttpServletRequest request) {

        String username = request.getUserPrincipal().getName();
        User user = userService.findByUsername(username);

        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        if ("PENDING".equals(task.getStatus())) {
            task.setStatus("DONE");
        } else {
            task.setStatus("PENDING");
        }

        return ResponseEntity.ok(taskService.updateTask(task));
    }

    /**
     * Añade e introduce una nueva subtarea asignada de forma obligatoria a una tarea padre existente.
     *
     * @param taskId Identificador único (ID) de la tarea contenedora.
     * @param subtask Objeto {@link Subtask} con los datos de la nueva subtarea.
     * @return El objeto {@link Subtask} guardado en el repositorio con su clave primaria generada.
     */
    @PostMapping("/{taskId}/subtasks")
    public Subtask addSubtask(@PathVariable Long taskId, @RequestBody Subtask subtask) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        subtask.setTask(task);
        return subtaskRepository.save(subtask);
    }

    /**
     * Modifica de forma binaria el estado de completado (true/false) de una subtarea.
     *
     * @param id Identificador único (ID) de la subtarea que se va a conmutar.
     * @return La entidad {@link Subtask} actualizada reflejando el cambio de estado.
     */
    @PatchMapping("/subtasks/{id}/toggle")
    public Subtask toggleSubtask(@PathVariable Long id) {
        Subtask s = subtaskRepository.findById(id).orElseThrow();
        s.setDone(!s.isDone());
        return subtaskRepository.save(s);
    }

    /**
     * Elimina de forma definitiva una subtarea del repositorio mediante su identificador.
     *
     * @param id Identificador único (ID) de la subtarea a eliminar.
     */
    @DeleteMapping("/subtasks/{id}")
    public void deleteSubtask(@PathVariable Long id) {
        subtaskRepository.deleteById(id);
    }

    /**
     * Aplica de forma masiva los nuevos índices de ordenación o cambios de columna de las tareas en el tablero Kanban.
     * Útil al arrastrar y soltar (drag and drop) tarjetas desde el Frontend de Angular.
     *
     * @param updates Listado de objetos DTO {@link TaskOrderUpdateDTO} con las tuplas de ID, nuevo estado y posición.
     * @return {@link ResponseEntity} vacío con estado sin contenido (200 OK) al finalizar la reestructuración.
     */
    @PostMapping("/reorder")
    public ResponseEntity<Void> reorderTasks(@RequestBody List<TaskOrderUpdateDTO> updates) {
        taskService.reorderTasks(updates);
        return ResponseEntity.ok().build();
    }
}