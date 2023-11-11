package org.space_fighter_client.util;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneChanger {

    public static <T> Object changeScene(ActionEvent event, Class<T> clazz, String template) throws IOException {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(clazz.getResource(template));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(clazz.getResource("login.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        return loader.getController();
    }

    public static <T> void changeScene(Stage stage, Class<T> clazz, String template) throws IOException {
        Parent root = FXMLLoader.load(clazz.getResource(template));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
}
