package org.space_fighter_client.game;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;
import org.space_fighter_client.game.objects.*;

import java.util.ArrayList;
import java.util.List;

public class World {
    private AnchorPane root;
    private final Stage stage;

    private Player player;

    private List<Enemy> enemies;

    private final double WIDTH;
    private final double WIDTH_MULTIPLIER = 3;
    private final double HEIGHT;
    private final double HEIGHT_MULTIPLIER = 1.5;

    public World(Stage stage, Position topLeftCorner, Position bottomRightCorner) {
        this.WIDTH = (bottomRightCorner.getX() - topLeftCorner.getX()) * WIDTH_MULTIPLIER;
        this.HEIGHT = (topLeftCorner.getY() - bottomRightCorner.getY()) * HEIGHT_MULTIPLIER;
        this.stage = stage;
        this.enemies = new ArrayList<>();
    }

    private JSONObject buildRequestWithToken(String command, List<String> arguments) {
        JSONObject req = new JSONObject();
        req.put("token", Main.getToken());
        req.put("command", command);
        req.put("arguments", arguments);
        return req;
    }

    public void start(JsonNode response) {
        root = new AnchorPane();
        addPlayer(response);
        addAsteroids(response);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        String endpoint = "/game";

        AnimationTimer launchTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long l) {
                if ((l - lastUpdate) >= 28_000_000) {
                    JSONObject req = buildRequestWithToken("look", new ArrayList<>());
                    updateWorld(ServerRequest.request(req.toString(), endpoint));
                    lastUpdate = l;
                }
            }
        };
        launchTimer.start();

        AnimationTimer wKey = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long l) {
                if ((l - lastUpdate) >= 28_000_000) {
                    JSONObject req = buildRequestWithToken("forward", List.of("1"));
                    updatePlayerPosition(ServerRequest.request(req.toString(), endpoint));
                    lastUpdate = l;
                }
            }
        };

        scene.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case A -> {
                    JSONObject req = buildRequestWithToken("turn", List.of("left"));
                    updateWorld(ServerRequest.request(req.toString(), endpoint));
                }
                case D -> {
                    JSONObject req = buildRequestWithToken("turn", List.of("right"));
                    updateWorld(ServerRequest.request(req.toString(), endpoint));
                }
                case W -> wKey.start();

            }
        });

        scene.setOnKeyReleased(key -> {
            switch (key.getCode()) {
                case W -> wKey.stop();
                case F -> {
                    JSONObject req = buildRequestWithToken("fire", new ArrayList<>());
                    addBullet(ServerRequest.request(req.toString(), endpoint));
                }
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    public void updateWorld(JsonNode response) {
        // System.out.println(response + "\n\n");
        removeEnemies();
        addEnemies(response);
    }

    private double[] convertResponseCoordsToLocal(JsonNode position) {
        double xAdjuster = (WIDTH/2);
        double yAdjuster = (HEIGHT/2);

        double x = position.get(0).asDouble();
        double y = position.get(1).asDouble();
        x = (x * WIDTH_MULTIPLIER) + xAdjuster;
        y = ((-1 * y) * HEIGHT_MULTIPLIER) + yAdjuster;
        return new double[] {x, y};
    }

    private double convertDirectionToDouble(String direction) {
        return switch(direction) {
            case "EAST" -> 0;
            case "NORTH" -> 90;
            case "WEST" -> 180;
            default -> 270;
        };
    }

    private void addPlayer(JsonNode response) {
        double[] convertedPlayerPos = convertResponseCoordsToLocal(response.get("data").get("status").get("position"));
        this.player = new Player(convertedPlayerPos[0], convertedPlayerPos[1]);
        this.player.setRotate(convertDirectionToDouble(response.get("data").get("status").get("direction").asText()));
        root.getChildren().add(player);
    }

    private void updatePlayerPosition(JsonNode response) {
        double[] convertedPlayerPos = convertResponseCoordsToLocal(response.get("data").get("status").get("position"));
        this.player.setRotate(convertDirectionToDouble(response.get("data").get("status").get("direction").asText()));
        this.player.setX(convertedPlayerPos[0]);
        this.player.setY(convertedPlayerPos[1]);
    }

    private void addAsteroids(JsonNode response) {
        for (JsonNode object : response.get("data").get("objects")) {
            if (object.get("type").asText().equalsIgnoreCase("ASTEROID")) {
                double[] convertedPos = convertResponseCoordsToLocal(object.get("position"));
                Asteroid asteroid = new Asteroid(convertedPos[0], convertedPos[1]);
                this.root.getChildren().add(asteroid);
            }
        }
    }

    private void addEnemies(JsonNode response) {
        for (JsonNode object : response.get("data").get("objects")) {
            if (object.get("type").asText().equalsIgnoreCase("ROBOT")) {
                double[] convertedPos = convertResponseCoordsToLocal(object.get("position"));
                Enemy enemy = new Enemy(convertedPos[0], convertedPos[1]);
                enemies.add(enemy);
            }
        }
        root.getChildren().addAll(enemies);
    }

    private void removeEnemies() {
        root.getChildren().removeAll(enemies);
        enemies.clear();
    }

    private void addBullet(JsonNode response) {
        System.out.println(response + "\n\n");
        JsonNode hitObject = response.get("data").get("hit_object");

        if (hitObject.get(0) != null) {
            double[] convertedPos = convertResponseCoordsToLocal(
                response.get("data").get("hit_object").get(0).get("position")
            );
            Bullet bullet = new Bullet(
                new Position((int)player.getX(), (int)player.getY()), 
                new Position((int)convertedPos[0], (int)convertedPos[1]),
                player.getRotate()
            );
            root.getChildren().add(bullet);
            bullet.move(root);
        }
    }
}
