package org.space_fighter_client.game;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.space_fighter_client.Main;
import org.space_fighter_client.communication.ServerRequest;
import org.space_fighter_client.game.Position;

import java.util.List;

public class World {

    private AnchorPane root;
    private Scene scene;
    private Stage stage;
    private final int WIDTH;
    private final int HEIGHT;

    public World(Stage stage, Position topLeftCorner, Position bottomRightCorner) {
        this.WIDTH = bottomRightCorner.getX() - topLeftCorner.getX();
        this.HEIGHT = topLeftCorner.getY() - bottomRightCorner.getY();
        this.stage = stage;
    }

    public void start() {
        root = new AnchorPane();
        scene = new Scene(root, WIDTH, HEIGHT);
        scene.setOnKeyPressed(key -> {
            JSONObject req = new JSONObject();
            switch (key.getCode()) {
                case A -> {
                    req.put("command", "turn");
                    req.put("arguments", List.of("left"));
                }
                case D -> {
                    req.put("command", "turn");
                    req.put("arguments", List.of("right"));
                }
                case W -> {
                    req.put("command", "forward");
                    req.put("arguments", List.of("1"));
                }
            }
            req.put("token", Main.getToken());
            updateWorld(ServerRequest.request(req.toString(), "/game"));
        });
        stage.setScene(scene);
        stage.show();
    }

    public void updateWorld(JsonNode response) {
        System.out.println(response);
    }
}
