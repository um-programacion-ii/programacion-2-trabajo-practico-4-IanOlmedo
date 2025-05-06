package repository.impl;
import modelo.*;
import repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final Map<Long, Usuario> usuarios = new HashMap<>();
    private Long nextDni = 1L;

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getDni() == null) {
            usuario.setDni(nextDni++);
        }
        usuarios.put(usuario.getDni(), usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> findByDni(Long dni) {
        return Optional.ofNullable(usuarios.get(dni));
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarios.values().stream()
                .filter(usuario -> usuario.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(usuarios.values());
    }

    @Override
    public void deleteByDni(Long dni) {
        usuarios.remove(dni);
    }

    @Override
    public boolean existsByDni(Long dni) {
        return usuarios.containsKey(dni);
    }
}