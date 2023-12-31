package org.space_fighter_client.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import org.json.JSONObject;
import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;
import org.space_fighter_client.util.SceneAlert;
import org.space_fighter_client.util.SceneChanger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label errorLabel;

    private JsonNode buildAndSendLoginRequest() {
        JSONObject request = new JSONObject();
        request.put("username", username.getText().trim());
        request.put("password", password.getText().trim());
        return ServerRequest.request(request.toString(), "/login");
    }

    private void showErrorMessage(String msg) {
        errorLabel.setText(msg);
    }

    public void login(ActionEvent event) throws IOException {
        if (username.getText().isBlank() || password.getText().isBlank()) {
            SceneAlert.warning("Empty fields", "Enter Username and Password");
        } else {
            JsonNode serverResponse = buildAndSendLoginRequest();
            System.out.println(serverResponse);
            if (serverResponse.get("result").asText().equals("error")) {
                showErrorMessage(serverResponse.get("message").asText());
            } else {
                Main.setToken(serverResponse.get("token").asText());
                LaunchViewController launchViewController = (LaunchViewController) SceneChanger.changeScene(event, getClass(), "LaunchView.fxml");
                launchViewController.setRobots(serverResponse.get("robots"));
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView imageView = new ImageView(getClass().getResource("background1.jpg").toExternalForm());
        imageView.setFitHeight(pane.getHeight());
        imageView.setFitWidth(pane.getWidth());
        pane.getChildren().add(0, imageView);
    }
}
