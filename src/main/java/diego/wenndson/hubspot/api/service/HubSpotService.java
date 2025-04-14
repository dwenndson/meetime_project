package diego.wenndson.hubspot.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import diego.wenndson.hubspot.api.controller.responses.AccessTokenResponse;
import diego.wenndson.hubspot.api.model.Contact;

public interface HubSpotService {
    AccessTokenResponse exchangeCodeForToken(String code) throws JsonProcessingException;
    boolean createContact(Contact contact, String accessToken);
    String generateUrlAuth();
}
