package site.sergeyfedorov.exceptions;

public class ServerLifecycleException extends RuntimeException {
    public ServerLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }
}
