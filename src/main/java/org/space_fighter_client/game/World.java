package org.space_fighter_client.game;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;
import org.space_fighter_client.controllers.LaunchViewController;
import org.space_fighter_client.game.objects.*;
import org.space_fighter_client.util.SceneChanger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private final String endpoint = "/game";

    private AnchorPane root;
    private final Stage stage;

    private Player player;
    private VBox playerInfo;
    private HBox shield;
    private HBox bullet;

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

    private void quit(AnimationTimer lookTimer) {
        lookTimer.stop();
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Quit?");
        alert.setContentText("Are you sure you want to quit?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            ServerRequest.request(buildRequestWithToken("quit", new ArrayList<>()).toString(), endpoint);
            try {
                SceneChanger.changeScene(stage, LaunchViewController.class, "LaunchView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lookTimer.start();
    }

    public void start(JsonNode response, String robotType) {
        root = new AnchorPane();
        this.playerInfo = new VBox();

        int iconSize = 50;
        this.shield = new HBox();
        this.shield.setPadding(new Insets(5));
        ImageView icon = new ImageView(Player.class.getResource("shield.png").toExternalForm());
        icon.setFitWidth(iconSize);
        icon.setFitHeight(iconSize);
        shield.getChildren().add(icon);
        shield.getChildren().add(new Text());

        this.bullet = new HBox();
        this.bullet.setPadding(new Insets(5));
        ImageView icon2 = new ImageView(Player.class.getResource("bullet.png").toExternalForm());
        icon2.setFitWidth(iconSize);
        icon2.setFitHeight(iconSize);
        bullet.getChildren().add(icon2);
        bullet.getChildren().add(new Text());

        this.playerInfo.setTranslateX(25);
        this.playerInfo.setTranslateY(25);
        playerInfo.getChildren().add(shield);
        playerInfo.getChildren().add(bullet);
        root.getChildren().add(playerInfo);

        addPlayer(response, robotType);
        addAsteroids(response);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        AnimationTimer lookTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long l) {
                if ((l - lastUpdate) >= 14_000_000) {
                    JSONObject req = buildRequestWithToken("look", new ArrayList<>());
                    updateWorld(ServerRequest.request(req.toString(), endpoint));
                    lastUpdate = l;
                }
            }
        };
        lookTimer.start();

        AnimationTimer wKey = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long l) {
                if ((l - lastUpdate) >= 14_000_000) {
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
                case R -> ServerRequest.request(
                    buildRequestWithToken("reload", new ArrayList<>()).toString(),
                    endpoint
                );
                case ESCAPE -> {
                    wKey.stop();
                    quit(lookTimer);
                }
            }
        });
        setBackgroundImage();
        stage.setScene(scene);
        stage.show();
    }

    public void updateWorld(JsonNode response) {
        removeEnemies();
        addEnemies(response);
        updatePlayerInfo(response);
    }

    private double convertCoordsToLocal(double coord, boolean w) {
        double multiplier = (w) ? WIDTH_MULTIPLIER : -HEIGHT_MULTIPLIER;
        double coordAdjuster = (w) ? (WIDTH/2) : (HEIGHT/2);
        return (coord * multiplier) + coordAdjuster;
    }

    private double[] convertResponseCoordsToLocal(JsonNode position) {
        return new double[] {
            convertCoordsToLocal(position.get(0).asDouble(), true),
            convertCoordsToLocal(position.get(1).asDouble(), false)
        };
    }

    private double convertDirectionToDouble(String direction) {
        return switch(direction) {
            case "EAST" -> 0;
            case "SOUTH" -> 90;
            case "WEST" -> 180;
            default -> 270;
        };
    }

    private void addPlayer(JsonNode response, String robotType) {
        System.out.println("\n\n" + response.toString());
        double[] convertedPlayerPos = convertResponseCoordsToLocal(response.get("data").get("status").get("position"));
        this.player = new Player(
            new Position(convertedPlayerPos[0], convertedPlayerPos[1]), 
            robotType,
            40,
            40
        );
        System.out.println(response.get("data").get("status").get("direction").asText());
        this.player.setRotate(convertDirectionToDouble(response.get("data").get("status").get("direction").asText()));
        root.getChildren().add(player);
    }

    private void updatePlayerInfo(JsonNode response) {
        int numberOfShield = response.get("data").get("status").get("shields").asInt();
        int numberOfShots = response.get("data").get("status").get("shots").asInt();
        
        Text shieldTxt = new Text("" + numberOfShield);
        shieldTxt.setFill(Color.WHITE);
        shield.getChildren().remove(shield.getChildren().size()-1);
        shield.getChildren().add(shieldTxt);

        Text bulletText = new Text("" + numberOfShots);
        bulletText.setFill(Color.WHITE);
        bullet.getChildren().remove(bullet.getChildren().size()-1);
        bullet.getChildren().add(bulletText);
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
                double topX = object.get("top_left_corner").get(0).asDouble();
                double topY = object.get("top_left_corner").get(1).asDouble();
                double bottomX = object.get("bottom_right_corner").get(0).asDouble();
                double bottomY = object.get("bottom_right_corner").get(1).asDouble();

                Asteroid asteroid = new Asteroid(
                    new Position(convertedPos[0], convertedPos[1])
                    // convertCoordsToLocal(bottomX - topX, true),
                    // convertCoordsToLocal(topY - bottomY, false)
                );
                this.root.getChildren().add(asteroid);
            }
        }
    }

    private void addEnemies(JsonNode response) {
        for (JsonNode object : response.get("data").get("objects")) {
            if (object.get("type").asText().equalsIgnoreCase("ROBOT")) {
                double[] convertedPos = convertResponseCoordsToLocal(object.get("position"));
                Enemy enemy = new Enemy(new Position(convertedPos[0], convertedPos[1]));
                enemy.setRotate(convertDirectionToDouble(object.get("direction").asText()));
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
        JsonNode hitObject = response.get("data").get("hit_object");
        Position bulletStart = new Position((int)player.getX(), (int)player.getY());
        Position bulletEnd = null;

        if (hitObject.get(0) != null) {
            double[] convertedPos = convertResponseCoordsToLocal(
                response.get("data").get("hit_object").get(0).get("position")
            );
            bulletEnd = new Position((int)convertedPos[0], (int)convertedPos[1]);
        }
        Bullet bullet = new Bullet(
            bulletStart, 
            bulletEnd,
            player.getRotate()
        );

        if (response.get("data").get("status").get("shots").asInt() > 0) {
            root.getChildren().add(bullet);
            bullet.move(root, new Position(WIDTH, HEIGHT));
        }
    }

    private void setBackgroundImage() {
        Random random = new Random();
        String pic = "game_background" + (random.nextInt(3) + 1) + ".png";
        ImageView imageView = new ImageView(LaunchViewController.class.getResource(pic).toExternalForm());
        imageView.setFitHeight(root.getHeight());
        imageView.setFitWidth(root.getWidth());
        root.getChildren().add(0, imageView);
    }
}
