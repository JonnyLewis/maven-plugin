package com.apim.service;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ApiGeneration {

    public static void main(String[] args) throws Exception {
        PluginPropertyValues.HOST = "localhost";
        PluginPropertyValues.ADMIN_PORT = 9443;
        PluginPropertyValues.GATEWAY_PORT = 8243;
        JsonObject jsonObject = registerUser();
        getAccessToken(jsonObject.getString("clientId"), jsonObject.getString("clientSecret"));


    }

    public static JsonObject registerUser() throws Exception {
        String url = "https://" + PluginPropertyValues.HOST + ":" + PluginPropertyValues.ADMIN_PORT + "/client-registration/v0.14/register";
        System.out.println(url);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            String request = Json.createObjectBuilder()
                    .add("callbackUrl", "www.google.lk")
                    .add("clientName", "rest_api_publisher")
                    .add("owner", "admin")
                    .add("grantType", "password refresh_token")
                    .add("saasApp", true).build().toString();
            httpPost.setEntity(new StringEntity(request));

            // Add headers
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
            httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
            httpPost.addHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(httpPost);
            JsonObject jsonObject = parseJson(EntityUtils.toString(response.getEntity()));
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonObject getAccessToken(String clientId, String clientSecret) {
        String url = "https://" + PluginPropertyValues.HOST + ":" + PluginPropertyValues.GATEWAY_PORT + "/token";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            // Add headers.
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("username", "admin"));
            params.add(new BasicNameValuePair("password", "admin"));
            params.add(new BasicNameValuePair("scope", "apim:api_create"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));


            // Add headers
            //httpPost.addHeader("Authorization", "Basic OEpmX3gxMkQwb2FlbG93TDQ5RlNTZlhqV2VzYTpNRnhuWDhxQnA2NG9aTHFscDE5SEluSjVNTXdh");
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(clientId, clientSecret);
            httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
            HttpResponse response = client.execute(httpPost);
            JsonObject jsonObject = parseJson(EntityUtils.toString(response.getEntity()));
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonObject parseJson(String json) throws Exception {
        JsonReader reader = Json.createReader(new StringReader(json));
        return reader.readObject();
    }
}
