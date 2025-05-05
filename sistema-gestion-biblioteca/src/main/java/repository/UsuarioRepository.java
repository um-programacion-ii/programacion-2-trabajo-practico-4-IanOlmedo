package repository;
import modelo.Usuario;
import java.util.List;

import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findByDni(Long dni);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findAll();
    void deleteByDni(Long dni);
    boolean existsByDni(Long dni);
}
