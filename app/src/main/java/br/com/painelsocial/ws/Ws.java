package br.com.painelsocial.ws;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import br.com.painelsocial.Config;
import br.com.painelsocial.request.CreateRequestRequest;
import br.com.painelsocial.request.LoginRequest;
import br.com.painelsocial.request.RegisterRequest;
import br.com.painelsocial.response.AuthenticationResponse;
import br.com.painelsocial.response.CreateRequestResponse;

public class Ws {

    private static final String BACKEND_URL = "http://painelsocial.herokuapp.com/";

    public static String register(String name, String email, String password) throws Exception {
        RegisterRequest obj = new RegisterRequest();
        obj.setName(name);
        obj.setEmail(email);
        obj.setPassword(password);

        AuthenticationResponse object = sendRequest(HttpMethod.POST, "register", obj, AuthenticationResponse.class);

        return object.getToken();
    }

    public static String login(String email, String password) throws Exception {
        LoginRequest obj = new LoginRequest();
        obj.setEmail(email);
        obj.setPassword(password);

        AuthenticationResponse object = sendRequest(HttpMethod.POST, "authenticate", obj, AuthenticationResponse.class);

        return object.getToken();
    }

    public static CreateRequestResponse.Request[] getAllRequests() throws Exception {
        CreateRequestResponse object = sendRequest(HttpMethod.GET, "demands", null, CreateRequestResponse.class);

        return object.getData();
    }

    public static boolean createRequest(String description, double lat, double lng) throws Exception {
        CreateRequestRequest obj = new CreateRequestRequest();
        obj.setDescription(description);
        obj.setLatitude(lat);
        obj.setLongitude(lng);

        sendRequest(HttpMethod.POST, "demands", obj, AuthenticationResponse.class);

        return true;
    }

    private static <E> E sendRequest(HttpMethod requestMethod, String method, Object requestObject, Class<E> returnClass) throws Exception {
        String requestUrl = BACKEND_URL + method;

        HttpHeaders requestHeaders = new HttpHeaders();

        requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        String token = Config.getInstance().getToken();
        if (token != null) {
            requestHeaders.set("x-access-token", token);
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestObject, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        ResponseEntity<E> responseEntity = restTemplate.exchange(requestUrl, requestMethod, requestEntity, returnClass);

        E responseObj = responseEntity.getBody();

        return responseObj;
    }

}
