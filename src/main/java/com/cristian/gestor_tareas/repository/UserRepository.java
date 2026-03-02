package com.cristian.gestor_tareas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cristian.gestor_tareas.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
