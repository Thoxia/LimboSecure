package com.thoxia.limbosecure.bot.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thoxia.limbosecure.bot.backend.http.HttpGetWithEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

@RequiredArgsConstructor
public class RequestHandler {

    private static final CloseableHttpClient CLIENT = HttpClients.createDefault();
    private static final Gson GSON = new Gson();

    private final String secretKey;
    private final String baseUrl;

    public HttpGetWithEntity initGetRequestWithBody(String additionUrl, String serverId) {
        HttpGetWithEntity request = new HttpGetWithEntity(baseUrl + "/" + additionUrl);
        request.addHeader("X-API-KEY", serverId);
        request.addHeader("Pragma", "no-cache");
        request.setHeader("Cache-Control", "no-cache");
        request.addHeader("Content-Type", "application/json");
        return request;
    }

    public HttpPut initPutRequest(String additionUrl, String serverId) {
        HttpPut request = new HttpPut(baseUrl + "/" + additionUrl);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("X-API-KEY", serverId);
        request.addHeader("X-API-Secret", secretKey);
        request.addHeader("Pragma", "no-cache");
        request.setHeader("Cache-Control", "no-cache");
        return request;
    }

    @SneakyThrows
    public Status isValid(String code, String discordId, String serverId) {
        HttpGetWithEntity request = initGetRequestWithBody("v1/code/status/" + code, serverId);
        JsonObject element = new JsonObject();
        element.addProperty("discordId", discordId);
        String json = GSON.toJson(element);
        request.setEntity(new StringEntity(json));

        URI uri = new URIBuilder(request.getURI()).build();
        request.setURI(uri);

        try (CloseableHttpResponse response = CLIENT.execute(request)) {
            final String string = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == 200) {
                JsonObject jsonObject = GSON.fromJson(string, JsonObject.class);
                return jsonObject.get("status").getAsString().equals("VALID") ? Status.VALID : Status.INVALID;
            }

            if (response.getStatusLine().getStatusCode() == 429) {
                return Status.RATE_LIMITED;
            }

            return Status.INVALID;
        }
    }

    @SneakyThrows
    public void verify(String code, String serverId) {
        HttpPut request = initPutRequest("v1/code/verify/" + code, serverId);
        URI uri = new URIBuilder(request.getURI()).build();
        request.setURI(uri);

        CloseableHttpResponse response = CLIENT.execute(request);
        response.close();
    }

    @SneakyThrows
    public void makeServerPremium(String id, String ip) {
        HttpPut request = initPutRequest("v1/server/" + id, id);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("ip", ip);
        object.addProperty("premium", true);
        String json = GSON.toJson(object);
        request.setEntity(new StringEntity(json));

        URI uri = new URIBuilder(request.getURI()).build();
        request.setURI(uri);

        CloseableHttpResponse response = CLIENT.execute(request);
        response.close();
    }

    public enum Status {

        INVALID,
        VALID,
        RATE_LIMITED,

    }

}
