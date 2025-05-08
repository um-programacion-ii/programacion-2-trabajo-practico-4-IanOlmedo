package modelo;

import java.time.LocalDate;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {
    private Long id;
    private Libro libroPrestado;
    private Usuario usuarioPrestatario;
    private LocalDate fechaPrestamo;
    private Optional<LocalDate> fechaDevolucion; // para que la devolucion sea opcional a la hora de recien sacar el prestamo
}