package repository.impl;
import modelo.*;
import org.springframework.stereotype.Repository;
import repository.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PrestamoRepositoryImpl implements PrestamoRepository {

    private final Map<Long, Prestamo> prestamos = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Prestamo save(Prestamo prestamo) {
        if (prestamo.getId() == null) {
            prestamo.setId(nextId++);
        }
        prestamos.put(prestamo.getId(), prestamo);
        return prestamo;
    }

    @Override
    public Optional<Prestamo> findById(Long id) {
        return Optional.ofNullable(prestamos.get(id));
    }

    @Override
    public List<Prestamo> findAll() {
        return new ArrayList<>(prestamos.values());
    }

    @Override
    public List<Prestamo> findByUsuarioDni(Long usuarioDni) {
        return prestamos.values().stream()
                .filter(prestamo -> prestamo.getUsuarioPrestatario().getDni().equals(usuarioDni))
                .collect(Collectors.toList());
    }

    @Override
    public List<Prestamo> findByLibroId(Long libroId) {
        return prestamos.values().stream()
                .filter(prestamo -> prestamo.getLibroPrestado().getId().equals(libroId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        prestamos.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return prestamos.containsKey(id);
    }
}