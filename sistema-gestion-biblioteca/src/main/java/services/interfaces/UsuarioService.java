package services.interfaces;
import modelo.*;
import java.util.List;

public interface UsuarioService {
    Usuario obtenerUsuarioPorDni(Long dni);
    Usuario obtenerUsuarioPorEmail(String email);
    List<Usuario> obtenerTodosUsuarios();
    Usuario guardarUsuario(Usuario usuario);
    void eliminarUsuario(Long dni);
    Usuario actualizarUsuario(Long dni, Usuario usuario);
    void bloquearUsuario(Long dni);
    void activarUsuario(Long dni);
}