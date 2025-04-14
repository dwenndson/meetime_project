package diego.wenndson.hubspot.api.service.impl.store;

import diego.wenndson.hubspot.api.controller.responses.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Service
public class TokenStore {
    private AccessTokenResponse accessTokenResponse;

    public AccessTokenResponse accessTokenResponse() {
        return accessTokenResponse;
    }

    public void setAccessTokenResponse(AccessTokenResponse accessTokenResponse){
        this.accessTokenResponse = accessTokenResponse;
    }
}
