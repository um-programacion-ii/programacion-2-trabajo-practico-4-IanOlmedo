package controller;
import modelo.*;
import services.interfaces.*;
import exceptions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        List<Usuario> usuarios = usuarioService.obtenerTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/dni}")
    public ResponseEntity<Usuario> obtenerUsuarioPorDni(@PathVariable Long dni) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorDni(dni);
            return ResponseEntity.ok(usuario);
        } catch (UsuarioNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/dni}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long dni, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(dni, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (UsuarioNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminar(@PathVariable Long dni) {
        try {
            usuarioService.eliminarUsuario(dni);
            return ResponseEntity.noContent().build();
        } catch (UsuarioNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{dni}/bloquear")
    public ResponseEntity<Void> bloquearUsuario(@PathVariable Long dni) {
        try {
            usuarioService.bloquearUsuario(dni);
            return ResponseEntity.ok().build();
        } catch (UsuarioNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{dni}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable Long dni) {
        try {
            usuarioService.activarUsuario(dni);
            return ResponseEntity.ok().build();
        } catch (UsuarioNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}