package org.space_fighter_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.space_fighter_client.communication.ServerRequest;

public class Main extends Application {
    private static final String apiBaseUrl = "http://localhost:5000";
    private static String token;

    public static void main(String[] args) {
        launch(args);
        JSONObject req = new JSONObject();
        req.put("token", getToken());
        ServerRequest.request(req.toString(), "/logout");
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token_) {
        token = token_;
    }

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }
}
