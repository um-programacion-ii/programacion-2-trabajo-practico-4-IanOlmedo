package exceptions;

public class LibroNoEncontradoException extends RuntimeException {

    public LibroNoEncontradoException(String message) {
        super(message);
    }
}