package modelo;
import estados.EstadoLibro;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class Libro {
    private Long id;
    private String isbn;
    private String titulo;
    private String autor;
    private EstadoLibro estadoLibro;

}
