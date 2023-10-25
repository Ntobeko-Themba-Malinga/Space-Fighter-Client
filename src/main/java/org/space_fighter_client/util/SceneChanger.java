package org.space_fighter_client.util;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneChanger {

    public static <T> void changeScene(ActionEvent event, Class<T> clazz, String template) throws IOException {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(clazz.getResource(template));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static <T> void changeScene(Stage stage, Class<T> clazz, String template) throws IOException {
        Parent root = FXMLLoader.load(clazz.getResource(template));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
}
