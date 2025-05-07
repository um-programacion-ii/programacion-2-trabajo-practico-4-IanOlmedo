package services.impl;
import estados.EstadoUsuario;
import exceptions.UsuarioNoEncontradoExcepcion;
import modelo.Usuario;
import repository.UsuarioRepository;
import services.interfaces.UsuarioService;
import java.util.List;

public class UsuarioServiceImpl implements UsuarioService{
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }
    @Override
    public Usuario obtenerUsuarioPorDni(Long dni) {
        return usuarioRepository.findByDni(dni)
                .orElseThrow(()->new UsuarioNoEncontradoExcepcion("Usuario no encontrado por el dni: "+dni));
    }

    @Override
    public Usuario obtenerUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email)
                .orElseThrow(()->new UsuarioNoEncontradoExcepcion("El usuario no fue encontrado por el emial: "+ email));
    }

    @Override
    public List<Usuario> obtenerTodosUsuarios(){
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario guardarUsuario(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(Long dni){
        if (!usuarioRepository.existsByDni(dni)){
            throw new UsuarioNoEncontradoExcepcion("No se puede eliminar el usuario con el DNI: "+dni+" porque no existe");
        }
        usuarioRepository.deleteByDni(dni);
    }

    @Override
    public Usuario actualizarUsuario(Long dni, Usuario usuario) {
        if (!usuarioRepository.existsByDni(dni)){
            throw new UsuarioNoEncontradoExcepcion("No se puede actualizar el usuario con el DNI: "+dni+" porque no existe");
        }
        usuario.setDni(dni);
        return usuarioRepository.save(usuario);
    }

    @Override
    public void bloquearUsuario(Long dni){
        Usuario usuario = obtenerUsuarioPorDni(dni);
        usuario.setEstadoUsuario(EstadoUsuario.BLOQUEADO);
        usuarioRepository.save(usuario);
    }

    @Override
    public void activarUsuario(Long dni) {
        Usuario usuario = obtenerUsuarioPorDni(dni);
        usuario.setEstadoUsuario(EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);
    }

}
