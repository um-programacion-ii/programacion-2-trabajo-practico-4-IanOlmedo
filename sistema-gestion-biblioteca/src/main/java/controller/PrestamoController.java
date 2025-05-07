package controller;
import services.interfaces.*;
import modelo.*;
import exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<List<Prestamo>> obtenerTodos() {
        List<Prestamo> prestamos = prestamoService.obtenerTodosPrestamos();
        return ResponseEntity.ok(prestamos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> obtenerPorId(@PathVariable Long id) {
        try {
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(id);
            return ResponseEntity.ok(prestamo);
        } catch (PrestamoNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Prestamo> crear(@RequestBody Prestamo prestamo) {
        Prestamo nuevoPrestamo = prestamoService.guardarPrestamo(prestamo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPrestamo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prestamo> actualizar(@PathVariable Long id, @RequestBody Prestamo prestamo) {

        try {
            Prestamo prestamoExistente = prestamoService.obtenerPrestamoPorId(id);
            if (prestamoExistente == null) {
                throw new PrestamoNoEncontradoExcepcion("Préstamo no encontrado con ID: " + id);
            }
            prestamoExistente.setFechaDevolucion(prestamo.getFechaDevolucion());
            Prestamo prestamoActualizado = prestamoService.guardarPrestamo(prestamoExistente); // Usamos el existente con los cambios
            return ResponseEntity.ok(prestamoActualizado);
        } catch (PrestamoNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            prestamoService.eliminarPrestamo(id);
            return ResponseEntity.noContent().build();
        } catch (PrestamoNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<Void> marcarComoDevuelto(@PathVariable Long id) {
        try {
            prestamoService.marcarComoDevuelto(id);
            return ResponseEntity.ok().build();
        } catch (PrestamoNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}