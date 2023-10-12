package org.space_fighter_client.communication;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class ServerRequestTest {

    @Test
    void request() {
        JSONObject request = new JSONObject();
        request.put("Test", "Test");
        JsonNode response = ServerRequest.request(request.toString(), "/");
        assertNotNull(response);
        assertEquals("error", response.get("result").asText());
        assertEquals("Bad json format", response.get("message").asText());
    }
}