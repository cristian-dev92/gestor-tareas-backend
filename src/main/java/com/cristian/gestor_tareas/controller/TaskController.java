package com.cristian.gestor_tareas.controller;

import com.cristian.gestor_tareas.model.Task;
import com.cristian.gestor_tareas.model.User;
import com.cristian.gestor_tareas.service.TaskService;
import com.cristian.gestor_tareas.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }


    //Obtener tareas del usuario autenticado
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(taskService.getTasksForUser(user.getId()));
    }

    //Crear tareas
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userService.findByUsername(username);
        Task saved = taskService.createTask(task, user);
        return ResponseEntity.ok(saved);
    }

    //Actualizar tareas
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return ResponseEntity.ok(taskService.updateTask(task));
    }

    //Borrar tareas
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

    //Modificar estado
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
}