package com.biblioteca.sistema_gestion_biblioteca.RepositoryTest;
import modelo.*;
import estados.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.impl.PrestamoRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PrestamoRepositoryImplTest {

    private PrestamoRepositoryImpl prestamoRepository;

    // Objetos dummy para Libro y Usuario que serán usados en los Prestamos
    private Libro libro1;
    private Libro libro2;
    private Usuario usuario;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        // Inicializa el repositorio antes de cada test para asegurar un estado limpio
        prestamoRepository = new PrestamoRepositoryImpl();

        // Inicializa los objetos dummy
        libro1 = new Libro(1L, "978-1234567890", "Clean Code", "Robert C. Martin", EstadoLibro.DISPONIBLE);
        libro2 = new Libro(2L, "978-0987654321", "The Pragmatic Programmer", "Andrew Hunt", EstadoLibro.DISPONIBLE);
        usuario = new Usuario(101L, "Juan Perez", "juan.perez@example.com", EstadoUsuario.ACTIVO);
        usuario2 = new Usuario(102L, "Maria Lopez", "maria.lopez@example.com", EstadoUsuario.ACTIVO);
    }

    @Test
    void testGuardarNuevoPrestamoAsignaId() {
        // Arrange
        Prestamo nuevoPrestamo = new Prestamo(
                null, // ID será asignado por el repositorio
                libro1,
                usuario,
                LocalDate.now(),
                Optional.empty() // Préstamo recién creado, sin fecha de devolución
        );

        // Act
        Prestamo savedPrestamo = prestamoRepository.save(nuevoPrestamo);

        // Assert
        assertNotNull(savedPrestamo);
        assertNotNull(savedPrestamo.getId());
        assertEquals(nuevoPrestamo.getLibroPrestado(), savedPrestamo.getLibroPrestado());
        assertEquals(nuevoPrestamo.getUsuarioPrestatario(), savedPrestamo.getUsuarioPrestatario());
        assertEquals(nuevoPrestamo.getFechaPrestamo(), savedPrestamo.getFechaPrestamo());
        assertTrue(savedPrestamo.getFechaDevolucion().isEmpty()); // Debe estar vacío al inicio

        Optional<Prestamo> foundPrestamo = prestamoRepository.findById(savedPrestamo.getId());
        assertTrue(foundPrestamo.isPresent());
        assertEquals(savedPrestamo, foundPrestamo.get());
    }

    @Test
    void testActualizarPrestamoExistenteConDevolucion() {
        // Arrange
        Prestamo existingPrestamo = new Prestamo(
                null,
                libro1,
                usuario,
                LocalDate.now().minusDays(5),
                Optional.empty()
        );
        Prestamo savedExistingPrestamo = prestamoRepository.save(existingPrestamo);

        // Actualizar el préstamo para incluir una fecha de devolución
        LocalDate fechaDevolucionReal = LocalDate.now();
        Prestamo updatedPrestamo = new Prestamo(
                savedExistingPrestamo.getId(), // Mismo ID
                libro1,
                usuario,
                savedExistingPrestamo.getFechaPrestamo(),
                Optional.of(fechaDevolucionReal) // Añadir fecha de devolución
        );

        // Act
        Prestamo result = prestamoRepository.save(updatedPrestamo);

        // Assert
        assertNotNull(result);
        assertEquals(savedExistingPrestamo.getId(), result.getId());
        assertTrue(result.getFechaDevolucion().isPresent());
        assertEquals(fechaDevolucionReal, result.getFechaDevolucion().get());

        Optional<Prestamo> foundPrestamo = prestamoRepository.findById(savedExistingPrestamo.getId());
        assertTrue(foundPrestamo.isPresent());
        assertEquals(updatedPrestamo, foundPrestamo.get());
    }

    @Test
    void testBuscarPorIdExistente() {
        // Arrange
        Prestamo prestamo = new Prestamo(
                null,
                libro1,
                usuario,
                LocalDate.now(),
                Optional.empty()
        );
        Prestamo savedPrestamo = prestamoRepository.save(prestamo);

        // Act
        Optional<Prestamo> foundPrestamo = prestamoRepository.findById(savedPrestamo.getId());

        // Assert
        assertTrue(foundPrestamo.isPresent());
        assertEquals(savedPrestamo.getId(), foundPrestamo.get().getId());
        assertEquals(savedPrestamo.getFechaPrestamo(), foundPrestamo.get().getFechaPrestamo());
    }

    @Test
    void testBuscarPorIdInexistenteRetornaVacio() {
        // Act
        Optional<Prestamo> foundPrestamo = prestamoRepository.findById(99L); // ID que no existe

        // Assert
        assertFalse(foundPrestamo.isPresent());
        assertTrue(foundPrestamo.isEmpty());
    }

    @Test
    void testFindAllRetornaListaVaciaSiNoHayPrestamos() {
        // Act
        List<Prestamo> prestamos = prestamoRepository.findAll();

        // Assert
        assertNotNull(prestamos);
        assertTrue(prestamos.isEmpty());
    }

    @Test
    void testFindAllRetornaTodosLosPrestamosGuardados() {
        // Arrange
        Prestamo prestamo1 = new Prestamo(null, libro1, usuario, LocalDate.now(), Optional.empty());
        Prestamo prestamo2 = new Prestamo(null, libro2, usuario2, LocalDate.now().minusDays(10), Optional.of(LocalDate.now().minusDays(5)));
        Prestamo savedPrestamo1 = prestamoRepository.save(prestamo1);
        Prestamo savedPrestamo2 = prestamoRepository.save(prestamo2);

        // Act
        List<Prestamo> prestamos = prestamoRepository.findAll();

        // Assert
        assertNotNull(prestamos);
        assertEquals(2, prestamos.size());
        assertTrue(prestamos.contains(savedPrestamo1));
        assertTrue(prestamos.contains(savedPrestamo2));
    }

    @Test
    void testEliminarPrestamoPorIdExistente() {
        // Arrange
        Prestamo prestamo = new Prestamo(null, libro1, usuario, LocalDate.now(), Optional.empty());
        Prestamo savedPrestamo = prestamoRepository.save(prestamo);
        Long idToDelete = savedPrestamo.getId();

        // Act
        prestamoRepository.deleteById(idToDelete);

        // Assert
        Optional<Prestamo> foundPrestamo = prestamoRepository.findById(idToDelete);
        assertFalse(foundPrestamo.isPresent());
    }

    @Test
    void testEliminarPrestamoPorIdInexistenteNoHaceNada() {
        // Arrange
        Prestamo prestamo = new Prestamo(null, libro1, usuario, LocalDate.now(), Optional.empty());
        prestamoRepository.save(prestamo);
        int initialSize = prestamoRepository.findAll().size();

        // Act
        prestamoRepository.deleteById(99L); // ID que no existe

        // Assert
        assertEquals(initialSize, prestamoRepository.findAll().size());
    }

    @Test
    void testExistePrestamoPorIdExistenteRetornaTrue() {
        // Arrange
        Prestamo prestamo = new Prestamo(null, libro2, usuario2, LocalDate.now(), Optional.empty());
        Prestamo savedPrestamo = prestamoRepository.save(prestamo);

        // Act & Assert
        assertTrue(prestamoRepository.existsById(savedPrestamo.getId()));
    }

    @Test
    void testExistePrestamoPorIdInexistenteRetornaFalse() {
        // Act & Assert
        assertFalse(prestamoRepository.existsById(99L));
    }
}