package diego.wenndson.hubspot.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import diego.wenndson.hubspot.api.config.HubSpotConfig;
import diego.wenndson.hubspot.api.controller.responses.AccessTokenResponse;
import diego.wenndson.hubspot.api.exception.HubSpotException;
import diego.wenndson.hubspot.api.service.HubSpotService;
import diego.wenndson.hubspot.api.service.impl.store.TokenStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private static final Logger logger = Logger.getLogger(OAuthController.class.getName());


    private final HubSpotService service;
    private final TokenStore tokenStore;

    public OAuthController(HubSpotService service, TokenStore tokenStore) {
        this.service = service;
        this.tokenStore = tokenStore;
    }


    @GetMapping("/authorize-url")
    public ResponseEntity<String> generateAuthorizationUrl() {
        logger.log(Level.INFO, "Gerando URL de autorização");
        return ResponseEntity.ok(service.generateUrlAuth());
    }

    @GetMapping("/callback")
    public ResponseEntity<String> processCallback(@RequestParam("code") String code) throws JsonProcessingException {
        try {
            logger.log(Level.INFO, "Recebido código de autorização: {0}", code);
            AccessTokenResponse tokenResponse = service.exchangeCodeForToken(code);
            tokenStore.setAccessTokenResponse(tokenResponse);
            logger.log(Level.INFO, "Token obtido com sucesso: {0}", tokenResponse.getAccessToken());
            return ResponseEntity.ok("Access Token armazenado com sucesso! Token: " + tokenResponse.getAccessToken());
        } catch (HubSpotException e) {
            logger.log(Level.SEVERE, "Erro ao processar callback OAuth: {0}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao processar callback OAuth.");
        }
    }
}