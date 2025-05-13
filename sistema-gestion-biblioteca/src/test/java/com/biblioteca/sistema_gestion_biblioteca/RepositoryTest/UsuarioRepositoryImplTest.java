package com.biblioteca.sistema_gestion_biblioteca.RepositoryTest;

import estados.EstadoUsuario;
import modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.impl.UsuarioRepositoryImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRepositoryImplTest {

    private UsuarioRepositoryImpl usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository = new UsuarioRepositoryImpl();
    }

    @Test
    void testGuardarNuevoUsuarioAsignaDni() {
        Usuario nuevoUsuario = new Usuario(null, "Juan Perez", "juan.perez@example.com", EstadoUsuario.ACTIVO);

        Usuario savedUsuario = usuarioRepository.save(nuevoUsuario);

        assertNotNull(savedUsuario);
        assertNotNull(savedUsuario.getDni()); // Verifica que se asignó un DNI
        assertEquals(nuevoUsuario.getNombre(), savedUsuario.getNombre());
        assertEquals(nuevoUsuario.getEmail(), savedUsuario.getEmail());
        assertEquals(nuevoUsuario.getEstadoUsuario(), savedUsuario.getEstadoUsuario()); // Verifica el estado

        Optional<Usuario> foundUsuario = usuarioRepository.findByDni(savedUsuario.getDni());
        assertTrue(foundUsuario.isPresent());
        assertEquals(savedUsuario, foundUsuario.get()); // Asegúrate de que equals y hashCode estén bien implementados
    }

    @Test
    void testActualizarUsuarioExistente() {
        Usuario existingUsuario = new Usuario(null, "Maria Lopez", "maria.lopez@example.com", EstadoUsuario.ACTIVO);
        Usuario savedExistingUsuario = usuarioRepository.save(existingUsuario); // Guardar primero para obtener un DNI

        Usuario updatedUsuario = new Usuario(savedExistingUsuario.getDni(), "Maria Gomez", "maria.gomez@example.com", EstadoUsuario.INACTIVO);

        Usuario result = usuarioRepository.save(updatedUsuario);

        assertNotNull(result);
        assertEquals(savedExistingUsuario.getDni(), result.getDni()); // El DNI debe permanecer el mismo
        assertEquals(updatedUsuario.getNombre(), result.getNombre());
        assertEquals(updatedUsuario.getEmail(), result.getEmail());
        assertEquals(updatedUsuario.getEstadoUsuario(), result.getEstadoUsuario()); // Verifica el estado

        Optional<Usuario> foundUsuario = usuarioRepository.findByDni(savedExistingUsuario.getDni());
        assertTrue(foundUsuario.isPresent());
        assertEquals(updatedUsuario.getNombre(), foundUsuario.get().getNombre());
        assertEquals(updatedUsuario.getEmail(), foundUsuario.get().getEmail());
        assertEquals(updatedUsuario.getEstadoUsuario(), foundUsuario.get().getEstadoUsuario());
    }

    @Test
    void testBuscarPorDniExistente() {
        Usuario usuario = new Usuario(null, "Carlos Garcia", "carlos.g@example.com", EstadoUsuario.ACTIVO);
        Usuario savedUsuario = usuarioRepository.save(usuario);

        Optional<Usuario> foundUsuario = usuarioRepository.findByDni(savedUsuario.getDni());

        assertTrue(foundUsuario.isPresent());
        assertEquals(savedUsuario.getDni(), foundUsuario.get().getDni());
        assertEquals(savedUsuario.getEmail(), foundUsuario.get().getEmail());
        assertEquals(savedUsuario.getEstadoUsuario(), foundUsuario.get().getEstadoUsuario());
    }

    @Test
    void testBuscarPorDniInexistenteRetornaVacio() {
        Optional<Usuario> foundUsuario = usuarioRepository.findByDni(99L); // DNI que no existe

        assertFalse(foundUsuario.isPresent());
        assertTrue(foundUsuario.isEmpty());
    }

    @Test
    void testBuscarPorEmailExistente() {
        // Arrange
        Usuario usuario1 = new Usuario(null, "Ana Ruiz", "ana.ruiz@example.com", EstadoUsuario.ACTIVO);
        Usuario usuario2 = new Usuario(null, "Pedro Diaz", "pedro.diaz@example.com", EstadoUsuario.BLOQUEADO);
        usuarioRepository.save(usuario1);
        usuarioRepository.save(usuario2);

        // Act
        Optional<Usuario> foundUsuario = usuarioRepository.findByEmail("ana.ruiz@example.com");

        // Assert
        assertTrue(foundUsuario.isPresent());
        assertEquals(usuario1.getEmail(), foundUsuario.get().getEmail());
        assertEquals(usuario1.getNombre(), foundUsuario.get().getNombre());
        assertEquals(usuario1.getEstadoUsuario(), foundUsuario.get().getEstadoUsuario());
    }

    @Test
    void testBuscarPorEmailInexistenteRetornaVacio() {
        // Act
        Optional<Usuario> foundUsuario = usuarioRepository.findByEmail("non.existent@example.com");

        // Assert
        assertFalse(foundUsuario.isPresent());
        assertTrue(foundUsuario.isEmpty());
    }

    @Test
    void testFindAllRetornaListaVaciaSiNoHayUsuarios() {

        List<Usuario> usuarios = usuarioRepository.findAll();

        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }

    @Test
    void testFindAllRetornaTodosLosUsuariosGuardados() {
        Usuario usuario1 = new Usuario(null, "Luisa", "luisa@example.com", EstadoUsuario.ACTIVO);
        Usuario usuario2 = new Usuario(null, "Roberto", "roberto@example.com", EstadoUsuario.ACTIVO);
        Usuario savedUsuario1 = usuarioRepository.save(usuario1);
        Usuario savedUsuario2 = usuarioRepository.save(usuario2);

        List<Usuario> usuarios = usuarioRepository.findAll();

        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());
        assertTrue(usuarios.contains(savedUsuario1));
        assertTrue(usuarios.contains(savedUsuario2));
    }

    @Test
    void testEliminarUsuarioPorDniExistente() {
        // Arrange
        Usuario usuario = new Usuario(null, "Elena", "elena@example.com", EstadoUsuario.INACTIVO);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        Long dniToDelete = savedUsuario.getDni();

        // Act
        usuarioRepository.deleteByDni(dniToDelete);

        // Assert
        Optional<Usuario> foundUsuario = usuarioRepository.findByDni(dniToDelete);
        assertFalse(foundUsuario.isPresent());
    }

    @Test
    void testEliminarUsuarioPorDniInexistenteNoHaceNada() {
        // Arrange
        Usuario usuario = new Usuario(null, "Fernando", "fernando@example.com", EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);
        int initialSize = usuarioRepository.findAll().size();

        usuarioRepository.deleteByDni(99L); // DNI que no existe

        assertEquals(initialSize, usuarioRepository.findAll().size());
    }

    @Test
    void testExisteUsuarioPorDniExistenteRetornaTrue() {
        // Arrange
        Usuario usuario = new Usuario(null, "Gema", "gema@example.com", EstadoUsuario.ACTIVO);
        Usuario savedUsuario = usuarioRepository.save(usuario);

        assertTrue(usuarioRepository.existsByDni(savedUsuario.getDni()));
    }

    @Test
    void testExisteUsuarioPorDniInexistenteRetornaFalse() {
        // Act & Assert
        assertFalse(usuarioRepository.existsByDni(99L));
    }
}