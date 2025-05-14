package com.biblioteca.sistema_gestion_biblioteca.ControllerTest;

import controller.UsuarioController;
import exceptions.UsuarioNoEncontradoExcepcion;
import modelo.Usuario;
import estados.EstadoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import services.interfaces.UsuarioService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario1 = new Usuario(12345678L, "Juan Perez", "juan.perez@example.com", EstadoUsuario.ACTIVO);
        usuario2 = new Usuario(98765432L, "Maria Gomez", "maria.gomez@example.com", EstadoUsuario.INACTIVO);
    }

    @Test
    void obtenerTodos_deberiaRetornarListaDeUsuariosYOk() {
        List<Usuario> usuariosEsperados = Arrays.asList(usuario1, usuario2);
        when(usuarioService.obtenerTodosUsuarios()).thenReturn(usuariosEsperados);

        ResponseEntity<List<Usuario>> respuesta = usuarioController.obtenerTodos();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(usuariosEsperados, respuesta.getBody());
        verify(usuarioService, times(1)).obtenerTodosUsuarios();
    }

    @Test
    void obtenerUsuarioPorDni_conDniExistente_deberiaRetornarUsuarioYOk() {
        when(usuarioService.obtenerUsuarioPorDni(12345678L)).thenReturn(usuario1);

        ResponseEntity<Usuario> respuesta = usuarioController.obtenerUsuarioPorDni(12345678L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(usuario1, respuesta.getBody());
        verify(usuarioService, times(1)).obtenerUsuarioPorDni(12345678L);
    }

    @Test
    void obtenerUsuarioPorDni_conDniNoExistente_deberiaRetornarNotFound() {
        when(usuarioService.obtenerUsuarioPorDni(99999999L)).thenThrow(new UsuarioNoEncontradoExcepcion("Usuario no encontrado por el dni: 99999999"));

        ResponseEntity<Usuario> respuesta = usuarioController.obtenerUsuarioPorDni(99999999L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(usuarioService, times(1)).obtenerUsuarioPorDni(99999999L);
    }

    @Test
    void crear_deberiaGuardarUsuarioYRetornarCreatedConNuevoUsuario() {
        when(usuarioService.guardarUsuario(usuario1)).thenReturn(usuario1);

        ResponseEntity<Usuario> respuesta = usuarioController.crear(usuario1);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(usuario1, respuesta.getBody());
        verify(usuarioService, times(1)).guardarUsuario(usuario1);
    }

    @Test
    void actualizar_conDniExistente_deberiaActualizarUsuarioYRetornarOkConUsuarioActualizado() {
        Usuario usuarioActualizado = new Usuario(12345678L, "Juan Carlos Perez", "juan.carlos@example.com", EstadoUsuario.ACTIVO);
        when(usuarioService.actualizarUsuario(12345678L, usuario2)).thenReturn(usuarioActualizado);

        ResponseEntity<Usuario> respuesta = usuarioController.actualizar(12345678L, usuario2);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(usuarioActualizado, respuesta.getBody());
        verify(usuarioService, times(1)).actualizarUsuario(12345678L, usuario2);
    }

    @Test
    void actualizar_conDniNoExistente_deberiaRetornarNotFound() {
        when(usuarioService.actualizarUsuario(99999999L, usuario2)).thenThrow(new UsuarioNoEncontradoExcepcion("No se puede actualizar el usuario con el DNI: 99999999 porque no existe"));

        ResponseEntity<Usuario> respuesta = usuarioController.actualizar(99999999L, usuario2);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(usuarioService, times(1)).actualizarUsuario(99999999L, usuario2);
    }

    @Test
    void eliminar_conDniExistente_deberiaEliminarUsuarioYRetornarNoContent() {
        doNothing().when(usuarioService).eliminarUsuario(12345678L);

        ResponseEntity<Void> respuesta = usuarioController.eliminar(12345678L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(usuarioService, times(1)).eliminarUsuario(12345678L);
    }

    @Test
    void eliminar_conDniNoExistente_deberiaRetornarNotFound() {
        doThrow(new UsuarioNoEncontradoExcepcion("No se puede eliminar el usuario con el DNI: 99999999 porque no existe")).when(usuarioService).eliminarUsuario(99999999L);

        ResponseEntity<Void> respuesta = usuarioController.eliminar(99999999L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(usuarioService, times(1)).eliminarUsuario(99999999L);
    }

    @Test
    void bloquearUsuario_conDniExistente_deberiaBloquearUsuarioYRetornarOk() {
        doNothing().when(usuarioService).bloquearUsuario(12345678L);

        ResponseEntity<Void> respuesta = usuarioController.bloquearUsuario(12345678L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(usuarioService, times(1)).bloquearUsuario(12345678L);
    }

    @Test
    void bloquearUsuario_conDniNoExistente_deberiaRetornarNotFound() {
        doThrow(new UsuarioNoEncontradoExcepcion("Usuario no encontrado por el dni: 99999999")).when(usuarioService).bloquearUsuario(99999999L);

        ResponseEntity<Void> respuesta = usuarioController.bloquearUsuario(99999999L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(usuarioService, times(1)).bloquearUsuario(99999999L);
    }

    @Test
    void activarUsuario_conDniExistente_deberiaActivarUsuarioYRetornarOk() {
        doNothing().when(usuarioService).activarUsuario(12345678L);

        ResponseEntity<Void> respuesta = usuarioController.activarUsuario(12345678L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(usuarioService, times(1)).activarUsuario(12345678L);
    }

    @Test
    void activarUsuario_conDniNoExistente_deberiaRetornarNotFound() {
        doThrow(new UsuarioNoEncontradoExcepcion("Usuario no encontrado por el dni: 99999999")).when(usuarioService).activarUsuario(99999999L);

        ResponseEntity<Void> respuesta = usuarioController.activarUsuario(99999999L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(usuarioService, times(1)).activarUsuario(99999999L);
    }
}