package com.jwt.repositories;

import com.jwt.models.Firstname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FirstnameRepository extends JpaRepository<Firstname, Long> {
    boolean existsByFirstname(String firstname);
    Optional<Firstname> findById(Long id);
    Optional<List<Firstname>> findByFirstname(String firstname);
    Optional<List<Firstname>> findByFirstnameIrish(String firstnameIrish);
}
