package com.desafio.literatura.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibros(
                @JsonAlias("title") String titulo,
                @JsonAlias("authors") List<DatosAutor> autores,
                @JsonAlias("languages") List<String> lenguajes,
                @JsonAlias("download_count") Integer descargas) {
}