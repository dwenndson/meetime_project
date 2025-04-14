package diego.wenndson.hubspot.api.service;

public interface SignatureService {
    boolean isSignatureValid(String payload, String signature);
}
