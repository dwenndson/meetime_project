package diego.wenndson.hubspot.api.controller;

import diego.wenndson.hubspot.api.controller.responses.AccessTokenResponse;
import diego.wenndson.hubspot.api.model.Contact;
import diego.wenndson.hubspot.api.service.HubSpotService;
import diego.wenndson.hubspot.api.service.impl.HubSpotServiceImpl;
import diego.wenndson.hubspot.api.service.impl.store.TokenStore;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final HubSpotService hubSpotService;
    private final TokenStore tokenStore;


    public ContactController(HubSpotService hubSpotService, TokenStore tokenStore) {
        this.hubSpotService = hubSpotService;
        this.tokenStore = tokenStore;
    }

    @PostMapping
    public ResponseEntity<String> createContact(@RequestBody Contact contact) {
        AccessTokenResponse tokenResponse = tokenStore.accessTokenResponse();

        if (tokenResponse == null || tokenResponse.getAccessToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token de acesso não encontrado. Realize o fluxo de OAuth primeiro.");
        }
        boolean success = hubSpotService.createContact(contact, tokenResponse.getAccessToken());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(success ? "Contato criado com sucesso." : "Falha ao criar contato");
    }
}
