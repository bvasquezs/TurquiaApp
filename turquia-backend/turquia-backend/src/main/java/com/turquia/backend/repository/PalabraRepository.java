package com.turquia.backend.repository;

import com.turquia.backend.model.Palabra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PalabraRepository extends JpaRepository<Palabra, Long> {
    // Método extra por si quieres filtrar por categoría desde Android
    List<Palabra> findByCategoria(String categoria);
}