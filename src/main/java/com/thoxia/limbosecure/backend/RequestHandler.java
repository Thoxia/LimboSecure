package com.thoxia.limbosecure.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thoxia.limbosecure.backend.http.HttpGetWithEntity;
import com.thoxia.limbosecure.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class RequestHandler {

    private static final CloseableHttpClient CLIENT = HttpClients.createDefault();
    public static final Executor ASYNC_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Gson GSON = new Gson();

    private final String baseUrl;
    private final String serverId;
    private final Logger logger;

    public HttpGetWithEntity initGetRequestWithBody(String additionUrl) {
        HttpGetWithEntity request = new HttpGetWithEntity(baseUrl + "/" + additionUrl);
        request.addHeader("X-API-KEY", serverId);
        request.addHeader("Pragma", "no-cache");
        request.setHeader("Cache-Control", "no-cache");
        request.addHeader("Content-Type", "application/json");
        return request;
    }

    public HttpPost initPostRequest(String additionUrl) {
        HttpPost request = new HttpPost(baseUrl + "/" + additionUrl);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("X-API-KEY", serverId);
        request.addHeader("Pragma", "no-cache");
        request.setHeader("Cache-Control", "no-cache");
        return request;
    }

    public boolean isVerified(String code, String discordId) {
        HttpGetWithEntity request = initGetRequestWithBody("v1/code/status/" + code);
        JsonObject element = new JsonObject();
        element.addProperty("discordId", discordId);
        String json = GSON.toJson(element);

        try {
            request.setEntity(new StringEntity(json));

            URI uri = new URIBuilder(request.getURI()).build();
            request.setURI(uri);

            try (CloseableHttpResponse response = CLIENT.execute(request)) {
                final String string = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonObject jsonObject = GSON.fromJson(string, JsonObject.class);
                    return jsonObject.get("status").getAsString().equals("VERIFIED");
                }

                if (response.getStatusLine().getStatusCode() == 429) {
                    logger.severe("Rate limited! Consider buying premium to support us.");
                    return false;
                }

                return false;
            }
        } catch (Exception exception) {
            // maybe our server is down, who knows...
            exception.printStackTrace();
            return true;
        }
    }

    public void createCode(String code, String discordId) {
        HttpPost request = initPostRequest("v1/code");
        JsonObject element = new JsonObject();
        element.addProperty("discordId", discordId);
        element.addProperty("code", code);
        String json = GSON.toJson(element);

        try {
            request.setEntity(new StringEntity(json));
            URI uri = new URIBuilder(request.getURI()).build();
            request.setURI(uri);
            CloseableHttpResponse response = CLIENT.execute(request);

            if (response.getStatusLine().getStatusCode() == 429) {
                logger.severe("Rate limited! Consider buying premium to support us.");
            }

            response.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
