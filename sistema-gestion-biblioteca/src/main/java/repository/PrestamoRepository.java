package repository;
import modelo.Prestamo;
import java.util.List;

import java.util.Optional;

public interface PrestamoRepository {
    Prestamo save(Prestamo prestamo);
    Optional<Prestamo> findById(Long id);
    List<Prestamo> findAll();
    List<Prestamo> findByUsuarioDni(Long usuarioDni); //Busquedas
    List<Prestamo> findByLibroId(Long libroId);
    void deleteById(Long id);
    boolean existsById(Long id);
}
