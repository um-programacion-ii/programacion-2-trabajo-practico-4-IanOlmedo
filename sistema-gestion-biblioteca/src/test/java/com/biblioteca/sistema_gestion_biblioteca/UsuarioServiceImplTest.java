package com.biblioteca.sistema_gestion_biblioteca;


import estados.EstadoUsuario;
import modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UsuarioRepository;
import services.impl.UsuarioServiceImpl;
import services.interfaces.UsuarioService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp(){
        usuario1 = new Usuario(1L, "Juan Perez", "juan.perez@email.com", EstadoUsuario.ACTIVO);
        usuario2 = new Usuario(2L, "María García", "maria.garcia@email.com", EstadoUsuario.INACTIVO);
    }

    @Test
    void testBuscarUsuarioPorDni(){
        Long dni = 1L;
        when(usuarioRepository.findByDni(dni)).thenReturn(Optional.of(usuario1));
        Usuario resultado = usuarioService.obtenerUsuarioPorDni(dni);

        assertNotNull(resultado);
        assertEquals(dni, resultado.getDni());
        assertEquals("Juan Perez", resultado.getNombre());
        verify(usuarioRepository).findByDni(dni);
    }
    @Test
    void testObtenerUsuarioPorEmail() {
        String email = "juan.perez@email.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario1));
        Usuario resultado = usuarioService.obtenerUsuarioPorEmail(email);

        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());
        assertEquals("Juan Perez", resultado.getNombre());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void testObtenerTodosUsuarios() {
        List<Usuario> usuarios = List.of(usuario1, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        List<Usuario> resultado = usuarioService.obtenerTodosUsuarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombre());
        assertEquals("María García", resultado.get(1).getNombre());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testGuardarUsuario() {
        Usuario usuarioAGuardar = new Usuario(null, "Carlos López", "carlos.lopez@email.com", EstadoUsuario.ACTIVO);
        Usuario usuarioGuardado = new Usuario(3L, "Carlos López", "carlos.lopez@email.com", EstadoUsuario.ACTIVO);
        when(usuarioRepository.save(usuarioAGuardar)).thenReturn(usuarioGuardado);
        Usuario resultado = usuarioService.guardarUsuario(usuarioAGuardar);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getDni());
        assertEquals("Carlos López", resultado.getNombre());
        verify(usuarioRepository).save(usuarioAGuardar);
    }

    @Test
    void testEliminarUsuario() {
        Long dniAEliminar = 1L;
        when(usuarioRepository.existsByDni(dniAEliminar)).thenReturn(true);
        usuarioService.eliminarUsuario(dniAEliminar);
        verify(usuarioRepository).deleteByDni(dniAEliminar);
    }

    @Test
    void testActualizarUsuario() {
        Long dniAActualizar = 1L;
        Usuario usuarioActualizado = new Usuario(dniAActualizar, "Juan Pérez Actualizado", "juan.perez.actualizado@email.com", EstadoUsuario.INACTIVO);
        when(usuarioRepository.existsByDni(dniAActualizar)).thenReturn(true);
        when(usuarioRepository.save(usuarioActualizado)).thenReturn(usuarioActualizado);
        Usuario resultado = usuarioService.actualizarUsuario(dniAActualizar, usuarioActualizado);

        assertNotNull(resultado);
        assertEquals("Juan Pérez Actualizado", resultado.getNombre());
        assertEquals("juan.perez.actualizado@email.com", resultado.getEmail());
        assertEquals(EstadoUsuario.INACTIVO, resultado.getEstadoUsuario());
        verify(usuarioRepository).save(usuarioActualizado);
    }

    @Test
    void testBloquearUsuario() {
        Long dniABloquear = 1L;
        when(usuarioRepository.findByDni(dniABloquear)).thenReturn(Optional.of(usuario1));
        usuarioService.bloquearUsuario(dniABloquear);
        assertEquals(EstadoUsuario.BLOQUEADO, usuario1.getEstadoUsuario());
        verify(usuarioRepository).findByDni(dniABloquear);
        verify(usuarioRepository).save(usuario1);
    }

    @Test
    void testActivarUsuario() {
        Long dniAActivar = 2L;
        when(usuarioRepository.findByDni(dniAActivar)).thenReturn(Optional.of(usuario2));
        usuarioService.activarUsuario(dniAActivar);
        assertEquals(EstadoUsuario.ACTIVO, usuario2.getEstadoUsuario());
        verify(usuarioRepository).findByDni(dniAActivar);
        verify(usuarioRepository).save(usuario2);
    }

}
