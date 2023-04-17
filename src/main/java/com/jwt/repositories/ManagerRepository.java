package com.jwt.repositories;

import com.jwt.models.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    List<Manager> findAll();
    Optional<Manager> findById(Long id);
    Optional<Manager> findByFirstnameAndLastname(String firstname, String lastname);
    Optional<List<Manager>> findByLastname(String lastname);
    Optional<List<Manager>> findByFirstname(String firstname);
    boolean existsByFirstnameAndLastname(String firstname, String lastname);
    Optional<Manager> findByEmail(String email);
}
