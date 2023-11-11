package org.space_fighter_client.controllers;

import java.io.IOException;
import java.util.List;

import javafx.scene.Node;
import javafx.stage.Stage;
import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;
import org.space_fighter_client.game.Position;
import org.space_fighter_client.game.World;
import org.space_fighter_client.util.SceneAlert;

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
    private World world;
    private JsonNode robots;

    private Stage getStage(ActionEvent event) {
        return (Stage)((Node) event.getSource()).getScene().getWindow();
    }

    private void buildWorld(ActionEvent event, JsonNode response) {
        for (JsonNode object : response.get("data").get("objects")) {
            if (object.get("type").asText().equalsIgnoreCase("world")) {
                Position topCorner = new Position(
                        object.get("topLeftCorner").get(0).asInt(),
                        object.get("topLeftCorner").get(1).asInt()
                );
                Position bottomCorner = new Position(
                        object.get("bottomRightCorner").get(0).asInt(),
                        object.get("bottomRightCorner").get(1).asInt()
                );
                this.world = new World(getStage(event), topCorner, bottomCorner);
            }
        }
    }

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
            System.out.println(serverResponse.toString());
            if (serverResponse.get("data").get("result").asText().equalsIgnoreCase("OK")) {
                buildWorld(event, serverResponse);
                this.world.start(serverResponse);
            } else {
                errorLabel.setText(serverResponse.get("data").get("message").asText());
            }
        }
    }

    public void setRobots(JsonNode robots) {
        this.robots = robots;
    }
}
