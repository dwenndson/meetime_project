package diego.wenndson.hubspot.api.service.impl;

import diego.wenndson.hubspot.api.config.HubSpotConfig;
import diego.wenndson.hubspot.api.service.SignatureService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class SignatureServiceImpl implements SignatureService {

    @Value("${secret.key}")
    private String secretKey;

    private final HubSpotConfig hubspotConfig;

    public SignatureServiceImpl(HubSpotConfig  hubSpotConfig){
        this.hubspotConfig = hubSpotConfig;
    }
    @Override
    public boolean isSignatureValid(String payload, String signature) {
        try {
            SecretKeySpec key = new SecretKeySpec(
                    hubspotConfig.getClientSecret().getBytes(StandardCharsets.UTF_8), secretKey);
            Mac mac = Mac.getInstance(secretKey);
            mac.init(key);
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedSignature = bytesToHex(rawHmac);
            return computedSignature.equalsIgnoreCase(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
