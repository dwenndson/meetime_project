package diego.wenndson.hubspot.api.controller;

import diego.wenndson.hubspot.api.config.HubSpotConfig;
import diego.wenndson.hubspot.api.service.SignatureService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger logger = Logger.getLogger(WebhookController.class.getName());

    private final HubSpotConfig hubSpotConfig;
    private final SignatureService signatureService;

    public WebhookController(HubSpotConfig hubSpotConfig, SignatureService signatureService) {
        this.hubSpotConfig = hubSpotConfig;
        this.signatureService = signatureService;
    }

    @PostMapping("/contact-created")
    public ResponseEntity<String> handleContactWebhook(
            @RequestBody String payload){
//          @RequestHeader(value = "X-HubSpot-Signature", required = false) String signature ){
//        Este valor seria caso, o projeto fosse para produção e necessário enviar o X-HubSpot-Signature
//        if (!signatureService.isSignatureValid(payload, signature)) {
//            logger.log(Level.SEVERE, "Assinatura inválida ou ausente no webhook.");
//            return new ResponseEntity<>("Signature inválida", HttpStatus.FORBIDDEN);
//        }
        logger.log(Level.INFO, "Webhook validado recebido: {0}", payload);
        return new ResponseEntity<>("Webhook processado com sucesso", HttpStatus.CREATED);
    }
}
