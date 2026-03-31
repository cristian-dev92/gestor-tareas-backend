package com.cristian.gestor_tareas.repository;

import com.cristian.gestor_tareas.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
}
