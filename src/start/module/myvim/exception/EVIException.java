package start.module.myvim.exception;


public class EVIException extends RuntimeException {
    
    public EVIException() {
        super("EVIException");
    }
    
    public EVIException(String es) {
        super(es);
    }
    
}