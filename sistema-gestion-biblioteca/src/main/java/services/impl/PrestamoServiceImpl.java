package services.impl;
import estados.EstadoLibro;
import exceptions.LibroNoEncontradoException;
import exceptions.PrestamoNoEncontradoExcepcion;
import modelo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.LibroRepository;
import repository.PrestamoRepository;
import services.interfaces.PrestamoService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    @Autowired
    public PrestamoServiceImpl(PrestamoRepository prestamoRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;

    }
    @Override
    public Prestamo realizarPrestamo(Libro libro, Usuario usuario){
        if (libro.getEstadoLibro() != EstadoLibro.DISPONIBLE){
            throw new LibroNoEncontradoException("El libro "+libro.getTitulo()+" no esta disponible");
        }
        Prestamo prestamo = new Prestamo();
        prestamo.setLibroPrestado(libro);
        prestamo.setUsuarioPrestatario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(null);

        libro.setEstadoLibro(EstadoLibro.PRESTADO);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);

    }
    @Override
    public Prestamo obtenerPrestamoPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new PrestamoNoEncontradoExcepcion("Prestamo no encontrado con ID: " + id));
    }

    @Override
    public List<Prestamo> obtenerTodosPrestamos() {
        return prestamoRepository.findAll();
    }

    @Override
    public List<Prestamo> obtenerPrestamosPorUsuario(Long usuarioId) {
        return prestamoRepository.findByUsuarioDni(usuarioId);
    }

    @Override
    public List<Prestamo> obtenerPrestamosPorLibro(Long libroId) {
        return prestamoRepository.findByLibroId(libroId);
    }

    @Override
    public void marcarComoDevuelto(Long id) {
        Optional<Prestamo> prestamoOptional = prestamoRepository.findById(id);
        if (prestamoOptional.isPresent()) {
            Prestamo prestamo = prestamoOptional.get();
            Libro libro = prestamo.getLibroPrestado();
            if (libro != null) {
                libro.setEstadoLibro(EstadoLibro.DISPONIBLE);
                prestamo.setFechaDevolucion(Optional.of(LocalDate.now()));
                prestamoRepository.save(prestamo);
            } else {
                System.err.println("Error: El libro asociado al préstamo con ID " + id + " es null.");
            }
        } else {
            throw new PrestamoNoEncontradoExcepcion("No se encontró el préstamo con ID: " + id);
        }
    }

    @Override
    public Prestamo guardarPrestamo(Prestamo prestamo) {
        return prestamoRepository.save(prestamo);
    }

    @Override
    public void eliminarPrestamo(Long id) {
        if (!prestamoRepository.existsById(id)) {
            throw new PrestamoNoEncontradoExcepcion("Prestamo no encontrado con ID: " + id);
        }
        prestamoRepository.deleteById(id);
    }
}
