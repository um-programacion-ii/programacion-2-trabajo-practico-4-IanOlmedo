package com.biblioteca.sistema_gestion_biblioteca.ServicesTest;

import estados.EstadoLibro;
import exceptions.LibroNoEncontradoException;
import modelo.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.LibroRepository;
import services.impl.LibroServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceImplTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroServiceImpl libroService;

    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
    libro1 = new Libro(1L, "978-0321765723", "The Lord of the Rings", "J.R.R. Tolkien", EstadoLibro.DISPONIBLE);
        libro2 = new Libro(2L, "978-0743273565", "The Great Gatsby", "F. Scott Fitzgerald", EstadoLibro.PRESTADO);
    }

    @Test
    void testBuscarPorIsbnCuandoExiste() {
        // Arrange
        String isbn = "978-0321765723";
        when(libroRepository.findByIsbn(isbn)).thenReturn(Optional.of(libro1));

        // Act
        Libro resultado = libroService.buscarPorIsbn(isbn);

        // Assert
        assertNotNull(resultado);
        assertEquals(isbn, resultado.getIsbn());
        assertEquals("The Lord of the Rings", resultado.getTitulo());
        verify(libroRepository).findByIsbn(isbn);
    }

    @Test
    void testBuscarPorIsbnCuandoNOExiste(){
        String isbn = "897-654";
        when(libroRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThrows(LibroNoEncontradoException.class, ()-> libroService.buscarPorIsbn(isbn));
        verify(libroRepository).findByIsbn(isbn);
    }

    @Test
    void testVerTodosLosLibros(){
        List<Libro> listaLibros = new ArrayList<>();
        listaLibros.add(libro1);
        listaLibros.add(libro2);
        when(libroRepository.findAll()).thenReturn(listaLibros);

        List<Libro> resultado = libroService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("The Lord of the Rings", resultado.get(0).getTitulo());
        verify(libroRepository).findAll();
    }

    @Test
    void testGuardarLibro(){
        Libro nuevoLibro = new Libro(null, "555-666", "El libro de la selva", "Ian Olmedo", EstadoLibro.DISPONIBLE);
        when(libroRepository.save(nuevoLibro)).thenReturn(new Libro(3L, "555-666", "El libro de la selva", "Ian Olmedo", EstadoLibro.DISPONIBLE));

        Libro resultado = libroService.guardar(nuevoLibro);
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("El libro de la selva", resultado.getTitulo());
        verify(libroRepository).save(nuevoLibro);
    }
    @Test
    void testEliminarLibroExistente() {
        // Arrange
        Long id = 1L;
        when(libroRepository.existsById(id)).thenReturn(true);
        doNothing().when(libroRepository).deleteById(id);

        // Act
        libroService.eliminar(id);

        // Assert
        verify(libroRepository).existsById(id);
        verify(libroRepository).deleteById(id);
    }

    @Test
    void testEliminarLibroNoExistente() {
        // Arrange
        Long id = 1L;
        when(libroRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(LibroNoEncontradoException.class, () -> libroService.eliminar(id));
        verify(libroRepository).existsById(id);
        verify(libroRepository, never()).deleteById(id);
    }

    @Test
    void testActualizarLibroExistente(){
        Long id = 1L;
        Libro libroActualizado = new Libro(id, "789-456", "Jorge el curioso", "Renzo Valverde", EstadoLibro.DISPONIBLE);
        when(libroRepository.existsById(id)).thenReturn(true);
        when(libroRepository.save(libroActualizado)).thenReturn(libroActualizado);

        // Act
        Libro resultado = libroService.actualizar(id, libroActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Jorge el curioso", resultado.getTitulo());
        assertEquals("Renzo Valverde", resultado.getAutor());
        assertEquals(EstadoLibro.DISPONIBLE, resultado.getEstadoLibro());
        verify(libroRepository).existsById(id);
        verify(libroRepository).save(libroActualizado);
    }

    @Test
    void testActualizarLibroInexistente() {
        // Arrange
        Long id = 1L;
        Libro libroActualizado = new Libro(id, "963-417", "Globant", "Hector", EstadoLibro.PRESTADO);
        when(libroRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(LibroNoEncontradoException.class, () -> libroService.actualizar(id, libroActualizado));
        verify(libroRepository).existsById(id);
        verify(libroRepository, never()).save(any(Libro.class));
    }
}
