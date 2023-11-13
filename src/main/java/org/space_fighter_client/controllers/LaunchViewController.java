package org.space_fighter_client.controllers;

import java.io.IOException;
import java.util.List;

import javafx.scene.Node;
import javafx.stage.Stage;
import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;
import org.space_fighter_client.game.Position;
import org.space_fighter_client.game.World;
import org.space_fighter_client.game.objects.Player;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import org.json.JSONObject;

public class LaunchViewController {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label errorLabel;
    private String robotType;
    private ToggleGroup robotsGroup = new ToggleGroup();

    private World world;

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
        RadioButton rButton = (RadioButton) robotsGroup.getSelectedToggle();
        if (rButton == null) return null;
        robotType = rButton.getText();

        JSONObject request = new JSONObject();
        request.put("command", "launch");
        request.put("arguments", List.of(robotType));
        request.put("token", Main.getToken());
        return ServerRequest.request(request.toString(), "/game");
    }

    public void launch(ActionEvent event) throws IOException {
        JsonNode serverResponse = buildAndSendLaunchRequest();
        if (serverResponse != null && serverResponse.get("data").get("result").asText().equalsIgnoreCase("OK")) {
            buildWorld(event, serverResponse);
            this.world.start(serverResponse, robotType);
        } else {
            errorLabel.setText("Choose a fighter!");
        }
    }

    public void setRobots(JsonNode robots) {
        ImageView backgroundImageView = new ImageView(getClass().getResource("background3.png").toExternalForm());
        backgroundImageView.setFitHeight(pane.getHeight());
        backgroundImageView.setFitWidth(pane.getWidth());
        pane.getChildren().add(0, backgroundImageView);
        HBox allRobotOptions = new HBox(); 

        for (JsonNode robot : robots.get("types")) {
            VBox robotInfo = new VBox();
            robotInfo.setPadding(new Insets(10));
            Color textFill = Color.WHITE;
            String robotType = robot.get("type").asText();
            RadioButton robotRadioButton = new RadioButton(robotType);

            robotInfo.getChildren().addAll(new Text(), new Text());
            robotRadioButton.setTextFill(textFill);
            robotRadioButton.setToggleGroup(robotsGroup);
            robotInfo.getChildren().add(robotRadioButton);

            ImageView imageView = new ImageView(Player.class.getResource(robotType + ".PNG").toExternalForm());
            imageView.setFitWidth(70);
            imageView.setFitHeight(70);
            robotInfo.getChildren().add(imageView);

            Text shots = new Text("Shots: " + robot.get("shots").asText());
            shots.setFill(textFill);
            Text shield = new Text("Shield: " + robot.get("shield").asText());
            shield.setFill(textFill);
            Text reload = new Text("Reload: " + robot.get("reload").asText());
            reload.setFill(textFill);
            Text travelDistance = new Text("Bullet Travel Distance: " + robot.get("bullet_travel_distance").asText());
            travelDistance.setFill(textFill);
            Text damage = new Text("Damage: " + robot.get("damage").asText());
            damage.setFill(textFill);

            robotInfo.getChildren().addAll(List.of(new Text(), shots, shield, reload, travelDistance, damage));
            allRobotOptions.getChildren().add(robotInfo);
        }
        allRobotOptions.setPadding(new Insets(50));
        pane.getChildren().add(allRobotOptions);
    }
}
