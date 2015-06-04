package br.com.painelsocial.ws;

import android.graphics.Bitmap;
import android.util.Base64;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.painelsocial.Config;
import br.com.painelsocial.model.Request;
import br.com.painelsocial.request.LoginRequest;
import br.com.painelsocial.request.RegisterRequest;
import br.com.painelsocial.response.AuthenticationResponse;
import br.com.painelsocial.response.ResponseWrapper;

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

    public static Request[] getAllRequests() throws Exception {
        ResponseWrapper<Request[]> object = sendRequest(HttpMethod.GET, "demands", null, new ParameterizedTypeReference<ResponseWrapper<Request[]>>() {});

        return object.getData();
    }

    public static Request loadRequest(String _id) throws Exception {
        ResponseWrapper<Request> object = sendRequest(HttpMethod.GET, "demands/" + _id, null, new ParameterizedTypeReference<ResponseWrapper<Request>>() {});

        return object.getData();
    }

    public static Request createRequest(String description, double lat, double lng, List<Bitmap> images) throws Exception {
        Request obj = new Request();
        obj.setDescription(description);
        obj.setLatitude(lat);
        obj.setLongitude(lng);

        List<Request.RequestImage> requestImages = new ArrayList<>();
        for (Bitmap image : images) {
            Request.RequestImage requestImage = new Request.RequestImage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

            requestImage.setImage(imageEncoded);
            requestImages.add(requestImage);
        }

        obj.setImages(requestImages.toArray(new Request.RequestImage[]{}));

        ResponseWrapper<Request> object = sendRequest(HttpMethod.POST, "demands", obj, new ParameterizedTypeReference<ResponseWrapper<Request>>() {});

        return object.getDemand();
    }

    private static <E> E sendRequest(HttpMethod requestMethod, String method, Object requestObject, Class<E> returnClass) throws Exception {
        return sendRequest(requestMethod, method, requestObject, returnClass, null);
    }

    private static <E> E sendRequest(HttpMethod requestMethod, String method, Object requestObject, ParameterizedTypeReference<E> parameterizedTypeReference) throws Exception {
        return sendRequest(requestMethod, method, requestObject, null, parameterizedTypeReference);
    }

    private static <E> E sendRequest(HttpMethod requestMethod, String method, Object requestObject, Class<E> returnClass, ParameterizedTypeReference<E> parameterizedTypeReference) throws Exception {
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


        ResponseEntity<E> responseEntity;
        if (returnClass != null) {
            responseEntity = restTemplate.exchange(requestUrl, requestMethod, requestEntity, returnClass);
        } else {
            responseEntity = restTemplate.exchange(requestUrl, requestMethod, requestEntity, parameterizedTypeReference);
        }

        E responseObj = responseEntity.getBody();

        return responseObj;
    }

}
