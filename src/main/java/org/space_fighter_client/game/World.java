package org.space_fighter_client.game;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;
import org.space_fighter_client.game.objects.Asteroid;
import org.space_fighter_client.game.objects.Player;

import java.util.List;
import java.util.Objects;

public class World {

    private AnchorPane root;
    private final Stage stage;

    private Player player;

    private final double WIDTH;
    private final double WIDTH_MULTIPLIER = 3;
    private final double HEIGHT;
    private final double HEIGHT_MULTIPLIER = 1.5;

    public World(Stage stage, Position topLeftCorner, Position bottomRightCorner) {
        this.WIDTH = (bottomRightCorner.getX() - topLeftCorner.getX()) * WIDTH_MULTIPLIER;
        this.HEIGHT = (topLeftCorner.getY() - bottomRightCorner.getY()) * HEIGHT_MULTIPLIER;
        this.stage = stage;
    }

    private JSONObject buildRequestWithToken() {
        JSONObject req = new JSONObject();
        req.put("token", Main.getToken());
        return req;
    }

    public void start(JsonNode response) {
        root = new AnchorPane();
        addPlayer(response);
        addAsteroids(response);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        String endpoint = "/game";

        AnimationTimer wKey = new AnimationTimer() {
            @Override
            public void handle(long l) {
                JSONObject req = buildRequestWithToken();
                req.put("command", "forward");
                req.put("arguments", List.of("1"));
                updatePlayerPosition(ServerRequest.request(req.toString(), endpoint));
            }
        };

        scene.setOnKeyPressed(key -> {
            JSONObject req = buildRequestWithToken();
            switch (key.getCode()) {
                case A -> {
                    req.put("command", "turn");
                    req.put("arguments", List.of("left"));
                    updateWorld(ServerRequest.request(req.toString(), "/game"));
                }
                case D -> {
                    req.put("command", "turn");
                    req.put("arguments", List.of("right"));
                    updateWorld(ServerRequest.request(req.toString(), "/game"));
                }
                case W -> wKey.start();

            }
        });

        scene.setOnKeyReleased(key -> {
            if (Objects.requireNonNull(key.getCode()) == KeyCode.W) {
                wKey.stop();
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    public void updateWorld(JsonNode response) {
        System.out.println(response);
    }

    private double[] convertResponseCoordsToLocal(JsonNode position) {
        double xAdjuster = (WIDTH/2);
        double yAdjuster = (HEIGHT/2);

        double x = position.get(0).asDouble();
        double y = position.get(1).asDouble();
        x = (x * WIDTH_MULTIPLIER) + xAdjuster;
        y = ((-1 * y) * HEIGHT_MULTIPLIER) + yAdjuster;
        System.out.println("x: " + x + " || y: " + y);
        return new double[] {x, y};
    }

    private void addPlayer(JsonNode response) {
        double[] convertedPlayerPos = convertResponseCoordsToLocal(response.get("data").get("status").get("position"));
        this.player = new Player(convertedPlayerPos[0], convertedPlayerPos[1]);
        root.getChildren().add(player);
    }

    private void updatePlayerPosition(JsonNode response) {
        double[] convertedPlayerPos = convertResponseCoordsToLocal(response.get("data").get("status").get("position"));
        this.player.setX(convertedPlayerPos[0]);
        this.player.setY(convertedPlayerPos[1]);
    }

    private void addAsteroids(JsonNode response) {
        System.out.println(response.get("data").get("objects"));
        for (JsonNode object : response.get("data").get("objects")) {
            if (object.get("type").asText().equalsIgnoreCase("ASTEROID")) {
                double[] convertedPos = convertResponseCoordsToLocal(object.get("position"));
                Asteroid asteroid = new Asteroid(convertedPos[0], convertedPos[1]);
                this.root.getChildren().add(asteroid);
            }
        }
    }
}
