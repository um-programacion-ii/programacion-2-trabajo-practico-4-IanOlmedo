package com.biblioteca.sistema_gestion_biblioteca.ControllerTest;

import controller.LibroController;
import exceptions.LibroNoEncontradoException;
import modelo.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import services.interfaces.LibroService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class LibroControllerTest {

    @InjectMocks
    private LibroController libroController;

    @Mock
    private LibroService libroService;

    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        libro1 = new Libro(1L, "978-0321765723", "El Señor de los Anillos", "J.R.R. Tolkien", null);
        libro2 = new Libro(2L, "978-0743273565", "El Gran Gatsby", "F. Scott Fitzgerald", null);
    }

    @Test
    void obteberTodos_deberiaRetornarListaDeLibrosYOk() {
        List<Libro> librosEsperados = Arrays.asList(libro1, libro2);
        when(libroService.obtenerTodos()).thenReturn(librosEsperados);

        ResponseEntity<List<Libro>> respuesta = libroController.obteberTodos();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(librosEsperados, respuesta.getBody());
        verify(libroService, times(1)).obtenerTodos();
    }

    @Test
    void obtenerPorId_conIdExistente_deberiaRetornarLibroYOk() {
        when(libroService.buscarPorId(1L)).thenReturn(libro1);

        ResponseEntity<Libro> respuesta = libroController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(libro1, respuesta.getBody());
        verify(libroService, times(1)).buscarPorId(1L);
    }

    @Test
    void obtenerPorId_conIdNoExistente_deberiaRetornarNotFound() {
        when(libroService.buscarPorId(3L)).thenThrow(new LibroNoEncontradoException("Libro no encontrado por el ID: 3"));

        ResponseEntity<Libro> respuesta = libroController.obtenerPorId(3L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(libroService, times(1)).buscarPorId(3L);
    }

    @Test
    void crear_deberiaGuardarLibroYRetornarCreatedConNuevoLibro() {
        when(libroService.guardar(libro1)).thenReturn(libro1);

        ResponseEntity<Libro> respuesta = libroController.crear(libro1);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(libro1, respuesta.getBody());
        verify(libroService, times(1)).guardar(libro1);
    }

    @Test
    void actualizar_conIdExistente_deberiaActualizarLibroYRetornarOkConLibroActualizado() {
        when(libroService.actualizar(1L, libro2)).thenReturn(libro2);

        ResponseEntity<Libro> respuesta = libroController.actualizar(1L, libro2);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(libro2, respuesta.getBody());
        verify(libroService, times(1)).actualizar(1L, libro2);
    }

    @Test
    void actualizar_conIdNoExistente_deberiaRetornarNotFound() {
        when(libroService.actualizar(3L, libro2)).thenThrow(new LibroNoEncontradoException("Libro no encontrado con ID: 3"));

        ResponseEntity<Libro> respuesta = libroController.actualizar(3L, libro2);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(libroService, times(1)).actualizar(3L, libro2);
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarLibroYRetornarNoContent() {
        doNothing().when(libroService).eliminar(1L);

        ResponseEntity<Void> respuesta = libroController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(libroService, times(1)).eliminar(1L);
    }

    @Test
    void eliminar_conIdNoExistente_deberiaRetornarNotFound() {
        doThrow(new LibroNoEncontradoException("Libro no encontrado con ID: 3")).when(libroService).eliminar(3L);

        ResponseEntity<Void> respuesta = libroController.eliminar(3L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(libroService, times(1)).eliminar(3L);
    }
}