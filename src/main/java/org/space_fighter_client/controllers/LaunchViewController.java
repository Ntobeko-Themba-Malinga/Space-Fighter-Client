package org.space_fighter_client.controllers;

import java.io.IOException;
import java.util.List;

import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;
import org.space_fighter_client.util.SceneAlert;
import org.space_fighter_client.util.SceneChanger;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.JSONObject;

public class LaunchViewController {
    @FXML
    private Label errorLabel;
    @FXML
    private TextField robotType;

    private JsonNode buildAndSendLaunchRequest() {
        JSONObject request = new JSONObject();
        request.put("command", "launch");
        request.put("arguments", List.of(robotType.getText().trim()));
        request.put("token", Main.getToken());
        return ServerRequest.request(request.toString(), "/game");
    }

    public void launch(ActionEvent event) throws IOException {
        if (robotType.getText().isEmpty()) {
            SceneAlert.warning("Empty field", "Enter robot type!");
        } else {
            JsonNode serverResponse = buildAndSendLaunchRequest();
            System.out.println(serverResponse);
            if (serverResponse.get("data").get("result").asText().equalsIgnoreCase("OK")) {
                SceneChanger.changeScene(event, getClass(), "Game.fxml");
            } else {
                errorLabel.setText(serverResponse.get("data").get("message").asText());
            }
        }
    }
}
