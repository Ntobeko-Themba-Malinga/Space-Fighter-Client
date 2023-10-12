package org.space_fighter_client.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label errorLabel;

    private void loadLaunchView(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("LaunchView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showEmptyFieldWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Empty fields");
        alert.setContentText("Enter Username and Password");
        alert.show();
    }

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
            showEmptyFieldWarning();
        } else {
            JsonNode serverResponse = buildAndSendLoginRequest();
            if (serverResponse.get("result").asText().equals("error")) {
                showErrorMessage(serverResponse.get("message").asText());
            } else {
                Main.setToken(serverResponse.get("token").asText());
                loadLaunchView(event);
            }
        }
    }
}
