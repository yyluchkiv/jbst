package jbst.ops.server.exceptions;

public class ServerOfflineException extends RuntimeException {

    public ServerOfflineException(RuntimeException ex) {
        super(ex);
    }
}
