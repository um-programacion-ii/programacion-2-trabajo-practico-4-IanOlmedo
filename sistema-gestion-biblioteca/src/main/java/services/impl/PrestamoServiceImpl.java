package services.impl;
import estados.EstadoLibro;
import exceptions.LibroNoEncontradoException;
import exceptions.PrestamoNoEncontradoExcepcion;
import modelo.*;
import repository.LibroRepository;
import repository.PrestamoRepository;
import services.interfaces.PrestamoService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

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
        Prestamo prestamo = obtenerPrestamoPorId(id);
        prestamo.setFechaDevolucion(Optional.of(LocalDate.now()));

        Libro libro = prestamo.getLibroPrestado();
        libro.setEstadoLibro(EstadoLibro.DISPONIBLE);
        libroRepository.save(libro); // Actualizar el estado del libro
        prestamoRepository.save(prestamo);
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
