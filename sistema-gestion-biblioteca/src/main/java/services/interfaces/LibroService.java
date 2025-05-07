package services.interfaces;
import modelo.*;
import java.util.List;

public interface LibroService {
    Libro buscarPorId(Long id);
    Libro buscarPorIsbn(String isbn);
    List<Libro> obtenerTodos();
    Libro guardar(Libro libro);
    void eliminar(Long id);
    Libro actualizar(Long id, Libro libro);
}