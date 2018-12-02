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

import javax.json.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApiGeneration {

    public static void main(String[] args) throws Exception {
        uploadApi("", null);
    }

    public static void deploy(ClassLoader loader) throws Exception {
        JsonObject clientCredentials = registerUser();
        JsonObject accessToken = getAccessToken(clientCredentials.getString("clientId"), clientCredentials.getString("clientSecret"));
        uploadApi(accessToken.getString("access_token"), loader);
    }

    /**
     * Get clientId & clientSecret.
     *
     * @return
     * @throws Exception
     */
    public static JsonObject registerUser() throws Exception {
        String url = "https://" + PluginPropertyValues.HOST + ":" + PluginPropertyValues.ADMINPORT + "/client-registration/v0.14/register";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            // Request payload.
            String request = Json.createObjectBuilder()
                    .add("callbackUrl", "www.google.lk")
                    .add("clientName", "rest_api_publisher")
                    .add("owner", PluginPropertyValues.USERNAME)
                    .add("grantType", "password refresh_token")
                    .add("saasApp", true).build().toString();
            httpPost.setEntity(new StringEntity(request));

            // Add headers
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
            httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
            httpPost.addHeader("Content-Type", "application/json");

            // Send request.
            HttpResponse response = client.execute(httpPost);

            // Parse response to JsonObject.
            JsonObject jsonObject = parseJson(EntityUtils.toString(response.getEntity()));
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get Access Token.
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    public static JsonObject getAccessToken(String clientId, String clientSecret) {
        String url = "https://" + PluginPropertyValues.HOST + ":" + PluginPropertyValues.GATEWAYPORT + "/token";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            // Add parameters.
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("username", PluginPropertyValues.USERNAME));
            params.add(new BasicNameValuePair("password", PluginPropertyValues.PASSWORD));
            params.add(new BasicNameValuePair("scope", "apim:api_create"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));


            // Add headers
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(clientId, clientSecret);
            httpPost.addHeader(new BasicScheme().authenticate(credentials, httpPost, null));

            // Send request.
            HttpResponse response = client.execute(httpPost);

            // Parse response to JsonObject.
            String result = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = parseJson(result);
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Upload API.
     *
     * @param accessToken
     * @param loader
     * @throws Exception
     */
    public static void uploadApi(String accessToken, ClassLoader loader) throws Exception {
        String url = "https://" + PluginPropertyValues.HOST + ":" + PluginPropertyValues.ADMINPORT + "/api/am/publisher/v0.14/apis";
        try (CloseableHttpClient client = HttpClients.createDefault();) {
            HttpPost httpPost = new HttpPost(url);

            // Add header.
            httpPost.addHeader("Authorization", "Bearer " + accessToken);
            httpPost.addHeader("Content-Type", "application/json");

            // endpoint_type
            JsonObjectBuilder epType = Json.createObjectBuilder().add("endpoint_type", "http");
            epType.add("production_endpoints", Json.createObjectBuilder().add("url", PluginPropertyValues.PRODUCTION).add("config", ""));
            epType.add("sandbox_endpoints", Json.createObjectBuilder().add("url", PluginPropertyValues.SANDBOX).add("config", ""));

            // transports
            JsonArrayBuilder transportArray = Json.createArrayBuilder();
            PluginPropertyValues.TRANSPORTS.forEach((t) -> {
                transportArray.add(t);
            });

            // tiers
            JsonArrayBuilder tiersArray = Json.createArrayBuilder();
            PluginPropertyValues.TIERS.forEach((t) -> {
                tiersArray.add(t);
            });

            String request = Json.createObjectBuilder()
                    .add("name", PluginPropertyValues.APINAME)
                    .add("description", PluginPropertyValues.DESCRIPTION)
                    .add("context", PluginPropertyValues.CONTEXT)
                    .add("version", PluginPropertyValues.VERSION)
                    .add("provider", PluginPropertyValues.USERNAME)
                    .add("apiDefinition", apiDefinition(loader))
                    .add("status", "CREATED")
                    .add("responseCaching", "Disabled")
                    .add("isDefaultVersion", false)
                    .add("type", PluginPropertyValues.TYPE)
                    .add("transport", transportArray.build())
                    .add("tiers", tiersArray.build())
                    .add("visibility", PluginPropertyValues.VISIBILITY)
                    .add("endpointConfig", epType.build().toString())
                    .add("gatewayEnvironments", PluginPropertyValues.GATEWAY)
                    .build().toString();
            System.out.println("###########Request###########\n" + request);
            httpPost.setEntity(new StringEntity(request));

            // Send request.
            HttpResponse response = client.execute(httpPost);
            System.out.println("###########Response###########\n" + EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse Json string
     *
     * @param json
     * @return
     * @throws Exception
     */
    public static JsonObject parseJson(String json) throws Exception {
        JsonReader reader = Json.createReader(new StringReader(json));
        return reader.readObject();
    }

    /**
     * Create API Definition reading classpath resource.
     *
     * @param loader
     * @return
     * @throws Exception
     */
    public static String apiDefinition(ClassLoader loader) throws Exception {
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = loader.getResourceAsStream(PluginPropertyValues.APIPATH);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String lines = bufferedReader.lines().collect(Collectors.joining());

            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bufferedReader.close();
        }
        return null;
    }
}
