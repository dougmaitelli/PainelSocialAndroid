package br.com.painelsocial.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by I837119 on 19/05/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationResponse {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
