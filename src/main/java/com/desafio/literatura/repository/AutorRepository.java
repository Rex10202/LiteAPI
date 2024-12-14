package com.desafio.literatura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.desafio.literatura.model.Autor;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombreContainsIgnoreCase(String name);

    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <= :year AND a.fechaDeFallecimiento >= :year")
    List<Autor> filterAuthorsByYear(int year);
}
