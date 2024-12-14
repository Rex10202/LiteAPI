package com.desafio.literatura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.desafio.literatura.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloContainsIgnoreCase(String titulo);

    @Query("SELECT l FROM Libro l WHERE l.lenguaje = :lan")
    List<Libro> filterLibrosByLanguage(String lan);
}