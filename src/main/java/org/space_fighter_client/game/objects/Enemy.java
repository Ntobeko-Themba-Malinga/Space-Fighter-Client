package org.space_fighter_client.game.objects;

import org.space_fighter_client.game.Position;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemy extends ImageView {
    private final int SIZE = 40;

    public Enemy(Position position) {
        setX(position.getX());
        setY(position.getY());
        setFitWidth(SIZE);
        setFitHeight(SIZE);
        setImage(new Image(getClass().getResource("enemy.PNG").toExternalForm()));
    }
}
