package services.impl;
import exceptions.LibroNoEncontradoException;
import repository.*;
import services.interfaces.LibroService;
import modelo.Libro;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;

    public LibroServiceImpl(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public Libro buscarPorIsbn(String isbn) {
        return libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new LibroNoEncontradoException("Libro no encontrado con ISBN: " + isbn));
    }

    @Override
    public List<Libro> obtenerTodos() {
        return libroRepository.findAll();
    }

    @Override
    public Libro guardar(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public void eliminar(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new LibroNoEncontradoException("Libro no encontrado con ID: " + id);
        }
        libroRepository.deleteById(id);
    }

    @Override
    public Libro actualizar(Long id, Libro libro) {
        if (!libroRepository.existsById(id)) {
            throw new LibroNoEncontradoException("Libro no encontrado con ID: " + id);
        }
        libro.setId(id); // Aseguramos que el ID sea el correcto para la actualización
        return libroRepository.save(libro);
    }
}