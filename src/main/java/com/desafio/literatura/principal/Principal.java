package com.desafio.literatura.principal;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.desafio.literatura.model.Autor;
import com.desafio.literatura.model.Datos;
import com.desafio.literatura.model.DatosLibros;
import com.desafio.literatura.model.Libro;
import com.desafio.literatura.repository.AutorRepository;
import com.desafio.literatura.repository.LibroRepository;
import com.desafio.literatura.service.ConsumeAPI;
import com.desafio.literatura.service.Conversor;

public class Principal {
    private final String URL_BASE = "https://gutendex.com/books/";
    private final String URL_SEARCH = "?search=";
    private final Scanner TECLADO = new Scanner(System.in);
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final Conversor conversor = new Conversor();
    private final ConsumeAPI consumeAPI = new ConsumeAPI();

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    private int option = -1;

    public void menu() {
        while (option != 0) {
            String MENU = """
                    Selecciona la opción ingresando el número correspondiente:
                    1- Buscar libro por título.
                    2- Listar libros registrados.
                    3- Listar autores registrados.
                    4- Listar autores vivos en un determinado año.
                    5- Listar libros por idioma.
                    0- Salir.
                    """;
            System.out.println(MENU);
            option = TECLADO.nextInt();
            TECLADO.nextLine();

            switch (option) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivoEnXAno();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("Cerrando la app...");
                default -> System.out.println("Opción invalida");
            }
        }
    }

    public Optional<DatosLibros> obtenerDatos(String libroTitulo) {
        String json = consumeAPI.obtenerDatos(URL_BASE + URL_SEARCH + libroTitulo.toLowerCase().replace(" ", "+"));
        List<DatosLibros> libros = conversor.convertData(json, Datos.class).resultados();

        Optional<DatosLibros> libro = libros.stream()
                .filter(l -> l.titulo().toLowerCase().contains(libroTitulo.toLowerCase()))
                .findFirst();

        return libro;
    }

    public void buscarLibroPorTitulo() {
        System.out.println("Ingresa el titulo del libro que deseas buscar: ");
        String titulo = TECLADO.nextLine();

        Optional<DatosLibros> libroAPI = obtenerDatos(titulo);
        Optional<Libro> libroDB = libroRepository.findByTituloContainsIgnoreCase(titulo);
        if (libroDB.isPresent()) {
            System.out.println("------- El libro registrado en la db -------");
            System.out.println(libroDB.get());
        } else if (libroAPI.isPresent()) {
            List<Autor> authorList = libroAPI.get().autores().stream()
                    .map(a -> autorRepository.findByNombreContainsIgnoreCase(a.nombre())
                            .orElseGet(() -> autorRepository.save(new Autor(a))))
                    .collect(Collectors.toList());
            // nueva instancia...
            Libro newlibroDB = new Libro(libroAPI.get(), authorList);
            libroRepository.save(newlibroDB);
            System.out.println(newlibroDB);
        } else {
            System.out.println("Libro no encontrado");
        }
    }

    public void listarLibrosRegistrados() {
        List<Libro> libroDBs = libroRepository.findAll();
        libroDBs.forEach(System.out::println);
        plantillaImpresion("libros", libroDBs.size());
    }

    public void listarAutoresRegistrados() {
        List<Autor> dbAuthors = autorRepository.findAll();
        dbAuthors.forEach(System.out::println);
        plantillaImpresion("autores", dbAuthors.size());
    }

    public void plantillaImpresion(String entidad, int tamano) {
        System.out.printf("Total de %s registrados: %s\n", entidad, tamano);
        System.out.println("------------");
    }

    public void listarAutoresVivoEnXAno() {
        System.out.println("Ingresa el año bajo el cual quieres consultar los autores que vivieron en dicha época: ");
        int ano = TECLADO.nextInt();
        TECLADO.nextLine();

        List<Autor> filteredAuthors = autorRepository.filterAuthorsByYear(ano);
        filteredAuthors.forEach(System.out::println);
    }

    public void listarLibrosPorIdioma() {
        List<String> lenguajes = List.of("es", "en", "fr", "pt");
        String lenguajeMenu = """
                Ingrese el idioma para buscar los libros:
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugues
                """;
        System.out.println(lenguajeMenu);
        String lenguaje = TECLADO.nextLine();
        // validacion...
        while (!lenguajes.contains(lenguaje)) {
            System.out.println("Opción invalida, ingresa un idioma de la lista: ");
            lenguaje = TECLADO.nextLine();
        }
        List<Libro> libroDBs = libroRepository.filterLibrosByLanguage(lenguaje);

        if (libroDBs.isEmpty()) {
            System.out.println("------------------------");
            System.out.println("No hay libros registrados con este idioma :(");
            System.out.println("------------------------");
        } else {
            libroDBs.forEach(System.out::println);
        }
    }

}
