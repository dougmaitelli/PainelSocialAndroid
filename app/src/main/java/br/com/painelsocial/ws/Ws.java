package br.com.painelsocial.ws;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Ws {

    private static final String BACKEND_URL = "http://painelsocial.herokuapp.com/";

    public static String login(String email, String password) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("email", email);
        obj.put("password", password);

        JSONObject object = sendRequest("authenticate", obj);

        return object.getString("token");
    }

    private static JSONObject sendRequest(String method, JSONObject requestObject) throws Exception {
        String requestUrl = BACKEND_URL + method;

        URL url = new URL(requestUrl);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String objectString = null;
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            OutputStream os = urlConnection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(requestObject.toString());
            osw.flush();
            osw.close();

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            objectString = sb.toString();
        } finally {
            urlConnection.disconnect();
        }

        return new JSONObject(objectString);
    }

}
