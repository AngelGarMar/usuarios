package com.transporte.usuarios.repositories;

import com.transporte.usuarios.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndEstatus(String username, Integer estatus);
    Optional<User> findByIdAndEstatus(Integer id, Integer estatus);
    List<User> findAllByEstatus(Integer estatus);
    Optional<User> findByUsername(String username);
}
