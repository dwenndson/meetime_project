package diego.wenndson.hubspot.api.exception;

public class HubSpotException extends RuntimeException {
    public HubSpotException(String message) {
        super(message);
    }

    public HubSpotException(String message, Throwable cause) {
        super(message, cause);
    }
}
