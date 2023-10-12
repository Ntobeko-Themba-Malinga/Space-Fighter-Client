package org.space_fighter_client.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.space_fighter_client.Main;

public class ServerRequest {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode request(String jsonMessage, String endpoint) {
        try {
            HttpResponse<String> response = Unirest.post(Main.getApiBaseUrl() + endpoint)
                    .body(jsonMessage)
                    .asString();
            return mapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
