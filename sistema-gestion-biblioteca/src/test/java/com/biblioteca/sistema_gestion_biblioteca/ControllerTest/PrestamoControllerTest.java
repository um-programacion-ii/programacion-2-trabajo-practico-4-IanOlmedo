package com.biblioteca.sistema_gestion_biblioteca.ControllerTest;


import controller.PrestamoController;
import exceptions.LibroNoEncontradoException;
import exceptions.PrestamoNoEncontradoExcepcion;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import services.interfaces.PrestamoService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class PrestamoControllerTest {

    @InjectMocks
    private PrestamoController prestamoController;

    @Mock
    private PrestamoService prestamoService;

    private Prestamo prestamo1;
    private Prestamo prestamo2;
    private Libro libro1;
    private Usuario usuario1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        libro1 = new Libro(1L, "123-456", "Título Libro 1", "Autor 1", null);
        usuario1 = new Usuario(11111111L, "Usuario 1", "usuario1@example.com", null);
        prestamo1 = new Prestamo(101L, libro1, usuario1, LocalDate.now(), Optional.empty());
        prestamo2 = new Prestamo(102L, new Libro(2L, "789-012", "Título Libro 2", "Autor 2", null),
                new Usuario(22222222L, "Usuario 2", "usuario2@example.com", null),
                LocalDate.now().minusDays(5), Optional.of(LocalDate.now()));
    }

    @Test
    void obtenerTodos_deberiaRetornarListaDePrestamosYOk() {
        List<Prestamo> prestamosEsperados = Arrays.asList(prestamo1, prestamo2);
        when(prestamoService.obtenerTodosPrestamos()).thenReturn(prestamosEsperados);

        ResponseEntity<List<Prestamo>> respuesta = prestamoController.obtenerTodos();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(prestamosEsperados, respuesta.getBody());
        verify(prestamoService, times(1)).obtenerTodosPrestamos();
    }

    @Test
    void obtenerPorId_conIdExistente_deberiaRetornarPrestamoYOk() {
        when(prestamoService.obtenerPrestamoPorId(101L)).thenReturn(prestamo1);

        ResponseEntity<Prestamo> respuesta = prestamoController.obtenerPorId(101L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(prestamo1, respuesta.getBody());
        verify(prestamoService, times(1)).obtenerPrestamoPorId(101L);
    }

    @Test
    void obtenerPorId_conIdNoExistente_deberiaRetornarNotFound() {
        when(prestamoService.obtenerPrestamoPorId(999L)).thenThrow(new PrestamoNoEncontradoExcepcion("Prestamo no encontrado con ID: 999"));

        ResponseEntity<Prestamo> respuesta = prestamoController.obtenerPorId(999L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(prestamoService, times(1)).obtenerPrestamoPorId(999L);
    }

    @Test
    void crear_deberiaGuardarPrestamoYRetornarCreatedConNuevoPrestamo() {
        when(prestamoService.guardarPrestamo(prestamo1)).thenReturn(prestamo1);

        ResponseEntity<Prestamo> respuesta = prestamoController.crear(prestamo1);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(prestamo1, respuesta.getBody());
        verify(prestamoService, times(1)).guardarPrestamo(prestamo1);
    }

    @Test
    void actualizar_conIdExistente_deberiaActualizarFechaDevolucionYRetornarOkConPrestamoActualizado() {
        Prestamo prestamoActualizado = new Prestamo(101L, libro1, usuario1, LocalDate.now(), Optional.of(LocalDate.now().plusDays(7)));
        when(prestamoService.obtenerPrestamoPorId(101L)).thenReturn(prestamo1);
        when(prestamoService.guardarPrestamo(any(Prestamo.class))).thenReturn(prestamoActualizado);

        ResponseEntity<Prestamo> respuesta = prestamoController.actualizar(101L, prestamoActualizado);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(prestamoActualizado, respuesta.getBody());
        verify(prestamoService, times(1)).obtenerPrestamoPorId(101L);
        verify(prestamoService, times(1)).guardarPrestamo(any(Prestamo.class));
    }

    @Test
    void actualizar_conIdNoExistente_deberiaRetornarNotFound() {
        when(prestamoService.obtenerPrestamoPorId(999L)).thenThrow(new PrestamoNoEncontradoExcepcion("Préstamo no encontrado con ID: 999"));

        ResponseEntity<Prestamo> respuesta = prestamoController.actualizar(999L, new Prestamo());

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(prestamoService, times(1)).obtenerPrestamoPorId(999L);
        verify(prestamoService, never()).guardarPrestamo(any(Prestamo.class));
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarPrestamoYRetornarNoContent() {
        doNothing().when(prestamoService).eliminarPrestamo(101L);

        ResponseEntity<Void> respuesta = prestamoController.eliminar(101L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(prestamoService, times(1)).eliminarPrestamo(101L);
    }

    @Test
    void eliminar_conIdNoExistente_deberiaRetornarNotFound() {
        doThrow(new PrestamoNoEncontradoExcepcion("Prestamo no encontrado con ID: 999")).when(prestamoService).eliminarPrestamo(999L);

        ResponseEntity<Void> respuesta = prestamoController.eliminar(999L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(prestamoService, times(1)).eliminarPrestamo(999L);
    }

    @Test
    void marcarComoDevuelto_conIdExistente_deberiaLlamarAlServicioYRetornarOk() {
        doNothing().when(prestamoService).marcarComoDevuelto(101L);

        ResponseEntity<Void> respuesta = prestamoController.marcarComoDevuelto(101L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(prestamoService, times(1)).marcarComoDevuelto(101L);
    }

    @Test
    void marcarComoDevuelto_conIdNoExistente_deberiaRetornarNotFound() {
        doThrow(new PrestamoNoEncontradoExcepcion("No se encontró el préstamo con ID: 999")).when(prestamoService).marcarComoDevuelto(999L);

        ResponseEntity<Void> respuesta = prestamoController.marcarComoDevuelto(999L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(prestamoService, times(1)).marcarComoDevuelto(999L);
    }
}