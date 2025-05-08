package com.biblioteca.sistema_gestion_biblioteca;
import estados.EstadoLibro;
import estados.EstadoUsuario;
import exceptions.PrestamoNoEncontradoExcepcion;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Usuario;
import repository.PrestamoRepository;
import services.impl.PrestamoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.LibroRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceImplTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock // Asegúrate de que LibroRepository también esté mockeado
    private LibroRepository libroRepository;

    @InjectMocks
    private PrestamoServiceImpl prestamoService;

    private Prestamo prestamo1;
    private Prestamo prestamo2;
    private Usuario usuario1;
    private Usuario usuario2;
    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
        // Inicializa los objetos Libro
        libro1 = new Libro(10L, "978-123", "Título 1", "Autor 1", EstadoLibro.PRESTADO);
        libro2 = new Libro(20L, "978-456", "Título 2", "Autor 2", EstadoLibro.DISPONIBLE);

        // Inicializa los objetos Usuario
        usuario1 = new Usuario(1L, "Ian Olmedo", "ianolmedo@gmail.com", EstadoUsuario.ACTIVO);
        usuario2 = new Usuario(1L, "Naomi Olmedo", "liznaolmedo@gmail.com", EstadoUsuario.ACTIVO);

        // Inicializa los objetos Prestamo utilizando los Libros y Usuarios inicializados
        prestamo1 = new Prestamo(1L, libro1, usuario1, LocalDate.now(), Optional.of(LocalDate.now().plusDays(7)));
        prestamo2 = new Prestamo(2L, libro2, usuario2, LocalDate.now(), Optional.of(LocalDate.now().plusDays(14)));
    }

    @Test
    void realizarPrestamoGuardarPrestamos() {
        // Arrange
        Libro libroAPrestar = new Libro(10L, "978-123", "Título Prestar", "Autor Prestar", EstadoLibro.DISPONIBLE);
        Usuario usuarioPrestando = new Usuario(20L, "Prestando Usuario", "prestando@email.com", EstadoUsuario.ACTIVO);
        LocalDate fechaPrestamoEsperada = LocalDate.now();
        LocalDate fechaDevolucionEsperada = LocalDate.now().plusDays(7);
        Prestamo nuevoPrestamo = new Prestamo(null, libroAPrestar, usuarioPrestando, fechaPrestamoEsperada, Optional.of(fechaDevolucionEsperada));
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(nuevoPrestamo);
        when(libroRepository.save(libroAPrestar)).thenReturn(libroAPrestar);

        // Act
        Prestamo resultado = prestamoService.realizarPrestamo(libroAPrestar, usuarioPrestando);

        // Assert
        assertNotNull(resultado);
        assertEquals(libroAPrestar, resultado.getLibroPrestado());
        assertEquals(usuarioPrestando, resultado.getUsuarioPrestatario());
        assertEquals(fechaPrestamoEsperada, resultado.getFechaPrestamo());
        assertEquals(fechaDevolucionEsperada, resultado.getFechaDevolucion().get()); // Accede al LocalDate dentro del Optional
        verify(prestamoRepository).save(any(Prestamo.class));
        verify(libroRepository).save(libroAPrestar);
    }

    @Test
    void obtenerPrestamosPorUsuarioConPrestamos() {
        // Arrange
        Long usuarioDni = 1L;
        List<Prestamo> prestamosUsuario = List.of(prestamo1);
        when(prestamoRepository.findByUsuarioDni(usuarioDni)).thenReturn(prestamosUsuario);

        // Act
        List<Prestamo> resultado = prestamoService.obtenerPrestamosPorUsuario(usuarioDni);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuario1, resultado.get(0).getUsuarioPrestatario());
        verify(prestamoRepository).findByUsuarioDni(usuarioDni);
    }

    @Test
    void obtenerPrestamosPorUsuarioDeUsuarioSinPrestamos() {
        // Arrange
        Long usuarioDni = 99L;
        when(prestamoRepository.findByUsuarioDni(usuarioDni)).thenReturn(new ArrayList<>());

        // Act
        List<Prestamo> resultado = prestamoService.obtenerPrestamosPorUsuario(usuarioDni);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(prestamoRepository).findByUsuarioDni(usuarioDni);
    }

    @Test
    void obtenerPrestamosPorLibroConPrestamos() {
        // Arrange
        Long libroId = 10L;
        List<Prestamo> prestamosLibro = new ArrayList<>();
        prestamosLibro.add(prestamo1);
        when(prestamoRepository.findByLibroId(libroId)).thenReturn(prestamosLibro);

        // Act
        List<Prestamo> resultado = prestamoService.obtenerPrestamosPorLibro(libroId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(libro1, resultado.get(0).getLibroPrestado());
        verify(prestamoRepository).findByLibroId(libroId);
    }

    @Test
    void obtenerPrestamosPorLibroSinHaberPerstamos() {
        // Arrange
        Long libroId = 99L;
        when(prestamoRepository.findByLibroId(libroId)).thenReturn(new ArrayList<>());

        // Act
        List<Prestamo> resultado = prestamoService.obtenerPrestamosPorLibro(libroId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(prestamoRepository).findByLibroId(libroId);
    }

    @Test
    void marcarComoDevueltoUsuarioExistente() {
        // Arrange
        Long idADevolver = 1L;
        when(prestamoRepository.findById(idADevolver)).thenReturn(Optional.of(prestamo1));
        Prestamo prestamoDevuelto = new Prestamo(1L, libro1, usuario1, LocalDate.now(), Optional.of(LocalDate.now().plusDays(7))); // Simula el mismo préstamo
        prestamoDevuelto.setFechaDevolucion(Optional.of(LocalDate.now()));
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoDevuelto); // Usa any(Prestamo.class) para mayor flexibilidad

        // Act
        prestamoService.marcarComoDevuelto(idADevolver);

        // Assert
        assertEquals(LocalDate.now(), prestamo1.getFechaDevolucion().get()); // Accede al LocalDate dentro del Optional
        verify(prestamoRepository).findById(idADevolver);
        verify(prestamoRepository).save(any(Prestamo.class)); // Usa any(Prestamo.class)
    }

    @Test
    void marcarComoDevueltoUsuarioNoExistente() {
        // Arrange
        Long idADevolver = 99L;
        when(prestamoRepository.findById(idADevolver)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PrestamoNoEncontradoExcepcion.class, () -> prestamoService.marcarComoDevuelto(idADevolver));
        verify(prestamoRepository).findById(idADevolver);
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }
}