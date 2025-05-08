package services.interfaces;
import modelo.*;
import java.util.List;

public interface PrestamoService {
    Prestamo realizarPrestamo(Libro libro, Usuario usuario);
    Prestamo obtenerPrestamoPorId(Long id);
    List<Prestamo> obtenerTodosPrestamos();
    List<Prestamo> obtenerPrestamosPorUsuario(Long usuarioDni);
    List<Prestamo> obtenerPrestamosPorLibro(Long libroId);
    void marcarComoDevuelto(Long id);
    Prestamo guardarPrestamo(Prestamo prestamo);
    void eliminarPrestamo(Long id);
}