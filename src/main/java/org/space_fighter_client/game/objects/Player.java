package org.space_fighter_client.game.objects;

import org.space_fighter_client.game.Position;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player extends ImageView {
    public Player(Position position, String type, double width, double height) {
        setX(position.getX());
        setY(position.getY());
        setFitWidth(width);
        setFitHeight(height);
        setImage(new Image(getClass().getResource(type + ".PNG").toExternalForm()));
    }
}
