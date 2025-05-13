package com.biblioteca.sistema_gestion_biblioteca.RepositoryTest;

import modelo.*;
import org.springframework.stereotype.Repository;
import repository.*;

import estados.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.impl.LibroRepositoryImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LibroRepositoryImplTest {

    private LibroRepositoryImpl libroRepository;

    @BeforeEach
    void setUp() {
        // Inicializa el repositorio antes de cada test para asegurar un estado limpio
        libroRepository = new LibroRepositoryImpl();
    }

    @Test
    void testGuardarNuevoLibroAsignaId() {
        // Arrange
        Libro newLibro = new Libro(null, "978-0321765723", "Effective Java", "Joshua Bloch", EstadoLibro.DISPONIBLE);

        // Act
        Libro savedLibro = libroRepository.save(newLibro);

        // Assert
        assertNotNull(savedLibro);
        assertNotNull(savedLibro.getId());
        assertEquals(newLibro.getIsbn(), savedLibro.getIsbn());
        assertEquals(newLibro.getTitulo(), savedLibro.getTitulo());
        assertEquals(newLibro.getAutor(), savedLibro.getAutor());
        assertEquals(newLibro.getEstadoLibro(), savedLibro.getEstadoLibro());

        Optional<Libro> foundLibro = libroRepository.findById(savedLibro.getId());
        assertTrue(foundLibro.isPresent());
        assertEquals(savedLibro, foundLibro.get());
    }

    @Test
    void testActualizarLibroExistente() {
        // Arrange
        Libro existingLibro = new Libro(null, "978-0321765723", "Effective Java", "Joshua Bloch", EstadoLibro.DISPONIBLE);
        Libro savedExistingLibro = libroRepository.save(existingLibro); // Guardar primero para obtener un ID

        Libro updatedLibro = new Libro(savedExistingLibro.getId(), "978-0321765723", "Effective Java (2nd Ed.)", "Joshua Bloch", EstadoLibro.PRESTADO);

        // Act
        Libro result = libroRepository.save(updatedLibro);

        // Assert
        assertNotNull(result);
        assertEquals(savedExistingLibro.getId(), result.getId()); // El ID debe permanecer el mismo
        assertEquals(updatedLibro.getTitulo(), result.getTitulo()); // El título debe estar actualizado
        assertEquals(updatedLibro.getEstadoLibro(), result.getEstadoLibro()); // El estado debe estar actualizado
        assertEquals(updatedLibro.getAutor(), result.getAutor());

        Optional<Libro> foundLibro = libroRepository.findById(savedExistingLibro.getId());
        assertTrue(foundLibro.isPresent());
        assertEquals(updatedLibro.getTitulo(), foundLibro.get().getTitulo());
        assertEquals(updatedLibro.getEstadoLibro(), foundLibro.get().getEstadoLibro());
    }

    @Test
    void testBuscarPorIdExistente() {
        // Arrange
        Libro libro = new Libro(null, "978-1234567890", "Clean Code", "Robert C. Martin", EstadoLibro.DISPONIBLE);
        Libro savedLibro = libroRepository.save(libro);

        // Act
        Optional<Libro> foundLibro = libroRepository.findById(savedLibro.getId());

        // Assert
        assertTrue(foundLibro.isPresent());
        assertEquals(savedLibro.getId(), foundLibro.get().getId());
        assertEquals(savedLibro.getTitulo(), foundLibro.get().getTitulo());
    }

    @Test
    void testBuscarPorIdInexistenteRetornaVacio() {
        // Act
        Optional<Libro> foundLibro = libroRepository.findById(99L); // ID que no existe

        // Assert
        assertFalse(foundLibro.isPresent());
        assertTrue(foundLibro.isEmpty());
    }

    @Test
    void testBuscarPorIsbnExistente() {
        // Arrange
        Libro libro1 = new Libro(null, "978-1234567890", "Clean Code", "Robert C. Martin", EstadoLibro.DISPONIBLE);
        Libro libro2 = new Libro(null, "978-0987654321", "The Pragmatic Programmer", "Andrew Hunt", EstadoLibro.PRESTADO);
        libroRepository.save(libro1);
        libroRepository.save(libro2);

        // Act
        Optional<Libro> foundLibro = libroRepository.findByIsbn("978-1234567890");

        // Assert
        assertTrue(foundLibro.isPresent());
        assertEquals(libro1.getIsbn(), foundLibro.get().getIsbn());
        assertEquals(libro1.getTitulo(), foundLibro.get().getTitulo());
    }

    @Test
    void testBuscarPorIsbnInexistenteRetornaVacio() {
        // Act
        Optional<Libro> foundLibro = libroRepository.findByIsbn("non-existent-isbn");

        // Assert
        assertFalse(foundLibro.isPresent());
        assertTrue(foundLibro.isEmpty());
    }

    @Test
    void testFindAllRetornaListaVaciaSiNoHayLibros() {
        // Act
        List<Libro> libros = libroRepository.findAll();

        // Assert
        assertNotNull(libros);
        assertTrue(libros.isEmpty());
    }

    @Test
    void testFindAllRetornaTodosLosLibrosGuardados() {
        // Arrange
        Libro libro1 = new Libro(null, "978-1", "Book One", "Author One", EstadoLibro.DISPONIBLE);
        Libro libro2 = new Libro(null, "978-2", "Book Two", "Author Two", EstadoLibro.PRESTADO);
        Libro savedLibro1 = libroRepository.save(libro1);
        Libro savedLibro2 = libroRepository.save(libro2);

        // Act
        List<Libro> libros = libroRepository.findAll();

        // Assert
        assertNotNull(libros);
        assertEquals(2, libros.size());
        assertTrue(libros.contains(savedLibro1));
        assertTrue(libros.contains(savedLibro2));
    }

    @Test
    void testEliminarLibroPorIdExistente() {
        // Arrange
        Libro libro = new Libro(null, "978-delete", "Book to Delete", "Author", EstadoLibro.DISPONIBLE);
        Libro savedLibro = libroRepository.save(libro);
        Long idToDelete = savedLibro.getId();

        // Act
        libroRepository.deleteById(idToDelete);

        // Assert
        Optional<Libro> foundLibro = libroRepository.findById(idToDelete);
        assertFalse(foundLibro.isPresent());
    }

    @Test
    void testEliminarLibroPorIdInexistenteNoHaceNada() {
        // Arrange
        Libro libro = new Libro(null, "978-some", "Some Book", "Author", EstadoLibro.DISPONIBLE);
        libroRepository.save(libro);
        int initialSize = libroRepository.findAll().size();

        // Act
        libroRepository.deleteById(99L); // ID que no existe

        // Assert
        assertEquals(initialSize, libroRepository.findAll().size());
    }

    @Test
    void testExisteLibroPorIdExistenteRetornaTrue() {
        // Arrange
        Libro libro = new Libro(null, "978-exists", "Book for Exists", "Author", EstadoLibro.DISPONIBLE);
        Libro savedLibro = libroRepository.save(libro);

        // Act & Assert
        assertTrue(libroRepository.existsById(savedLibro.getId()));
    }

    @Test
    void testExisteLibroPorIdInexistenteRetornaFalse() {
        // Act & Assert
        assertFalse(libroRepository.existsById(99L));
    }
}