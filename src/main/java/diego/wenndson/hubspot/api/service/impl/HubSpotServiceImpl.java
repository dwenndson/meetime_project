package diego.wenndson.hubspot.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import diego.wenndson.hubspot.api.config.HubSpotConfig;
import diego.wenndson.hubspot.api.controller.responses.AccessTokenResponse;
import diego.wenndson.hubspot.api.model.Contact;
import diego.wenndson.hubspot.api.exception.HubSpotException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diego.wenndson.hubspot.api.model.wrapper.ContactWrapper;
import diego.wenndson.hubspot.api.service.HubSpotService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class HubSpotServiceImpl implements HubSpotService {

    private static final Logger logger = Logger.getLogger(HubSpotService.class.getName());

    private final HubSpotConfig hubSpotConfig;

    public HubSpotServiceImpl(HubSpotConfig hubSpotConfig) {
        this.hubSpotConfig = hubSpotConfig;
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generateUrlAuth() {
        return UriComponentsBuilder.fromUriString(hubSpotConfig.getAuthBaseUrl())
                .queryParam("client_id", hubSpotConfig.getClientId())
                .queryParam("redirect_uri", hubSpotConfig.getRedirectUri())
                .queryParam("scope", hubSpotConfig.getScope())
                .queryParam("response_type", "code")
                .build()
                .toUriString();
    }

    public AccessTokenResponse exchangeCodeForToken(String code) throws JsonProcessingException {
        try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", hubSpotConfig.getClientId());
        formData.add("client_secret", hubSpotConfig.getClientSecret());
        formData.add("redirect_uri", hubSpotConfig.getRedirectUri());
        formData.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                hubSpotConfig.getTokenUrl(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.log(Level.INFO, "Troca de token realizada com sucesso.. Response: {0}", response.getBody());
                return objectMapper.readValue(response.getBody(), AccessTokenResponse.class);
            } else {
                logger.log(Level.SEVERE, "Falha ao trocar o código pelo token, status: {0}", response.getStatusCode());
                throw new HubSpotException("Falha ao trocar o código pelo token, status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            logger.log(Level.SEVERE, "Erro HTTP ao trocar o código pelo token: {0}", e.getResponseBodyAsString());
        throw new HubSpotException("Erro ao trocar o código pelo token: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro inesperado ao trocar o código pelo token", e);
            throw new HubSpotException("Erro inesperado ao trocar o código pelo token", e);
        }
    }

    public boolean createContact(Contact contact, String accessToken){
        String hubspotCreateContactUrl = "https://api.hubapi.com/crm/v3/objects/contacts";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalArgumentException("Access token é obrigatório para criar um contato.");
        }
        headers.set("Authorization", "Bearer " + accessToken);

        try {

            String jsonBody = objectMapper.writeValueAsString(new ContactWrapper(contact));

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    hubspotCreateContactUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                logger.log(Level.INFO, "Contato criado com sucesso. Resposta: {0}", response.getBody());
                return true;
            } else {
                logger.log(Level.SEVERE, "Falha ao criar contato. Status: {0}, Resposta: {1}",
                        new Object[]{response.getStatusCode(), response.getBody()});
                return false;
            }
        } catch (HttpClientErrorException e) {
            logger.log(Level.SEVERE, "Erro HTTP ao criar contato: {0}", e.getResponseBodyAsString());
            throw new HubSpotException("Erro ao criar contato: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro inesperado ao criar contato", e);
            throw new HubSpotException("Erro inesperado ao criar contato", e);
        }
    }

}
