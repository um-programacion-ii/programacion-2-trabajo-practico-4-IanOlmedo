package controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.interfaces.*;
import modelo.*;
import exceptions.*;


import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService){
        this.libroService = libroService;
    }
    @GetMapping
    public ResponseEntity<List<Libro>> obteberTodos() {
        List<Libro> libros = libroService.obtenerTodos();
        return ResponseEntity.ok(libros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerPorId(@PathVariable Long id) {
        try {
            Libro libro = libroService.buscarPorId(id); //
            return ResponseEntity.ok(libro);
        } catch (LibroNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Libro> crear(@RequestBody Libro libro){
        Libro nuevoLibro = libroService.guardar(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizar(@PathVariable Long id, @RequestBody Libro libro){
        try {
            Libro libroActualizado = libroService.actualizar(id, libro);
            return ResponseEntity.ok(libroActualizado);
        } catch (LibroNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            libroService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (LibroNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}