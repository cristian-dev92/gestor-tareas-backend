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

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    // Inyección de dependencias por constructor
    public TaskService(TaskRepository taskRepository) {

        this.taskRepository = taskRepository;
    }

    // Método para reordenar las tareas
    public void reorderTasks(List<TaskOrderUpdateDTO> updates) {
        Map<Long, Integer> map = updates.stream()
                .collect(Collectors.toMap(TaskOrderUpdateDTO::getId, TaskOrderUpdateDTO::getOrderIndex));

        List<Task> tasks = taskRepository.findAllById(map.keySet());

        for (Task task : tasks) {
            Integer newOrder = map.get(task.getId());
            task.setOrderIndex(newOrder);
        }

        taskRepository.saveAll(tasks);
    }

    // Método para obtener todas las tareas de un usuario específico
    public List<Task> getTasksForUser(Long userId) {

        return taskRepository.findByUserId(userId);
    }

    // Método para crear una nueva tarea asociada a un usuario
    public Task createTask(Task task, User user) {
        task.setUser(user);

        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }

        if (task.getPriority() == null) {
            task.setPriority("MEDIUM");
        }

        task.setCreatedAt(java.time.LocalDateTime.now()); //Añadimos la fecha de creación

        return taskRepository.save(task);
    }

    // Método para actualizar una tarea existente
    public Task updateTask(Task task) {

        return taskRepository.save(task);
    }

    // Método para eliminar una tarea por su ID
    public void deleteTask(Long id) {

        taskRepository.deleteById(id);
    }

    //Método para obtener tareas por estado
    public List<Task> getTasksByStatus(String status) {

        return taskRepository.findByStatus(status);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

}


