package org.space_fighter_client.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerRequest {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode request(String jsonMessage) {
        try {
            return mapper.readTree("");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
