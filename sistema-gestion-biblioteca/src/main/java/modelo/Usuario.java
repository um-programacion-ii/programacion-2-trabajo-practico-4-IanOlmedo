package modelo;
import estados.EstadoUsuario;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Usuario {
    private Long dni;
    private String nombre;
    private String email;
    private EstadoUsuario estadoUsuario;
}
