package org.space_fighter_client.game.objects;

import java.util.Random;

import org.space_fighter_client.game.Position;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Asteroid extends ImageView {
    public Asteroid(Position position, double width, double height) {
        setX(position.getX());
        setY(position.getY());
        setFitWidth(width);
        setFitHeight(height);
        Random random = new Random();
        String pic = "asteroid" + ((random.nextInt(3)) + 1) + ".png";
        setImage(new Image(getClass().getResource(pic).toExternalForm()));
    }

    public Asteroid(Position position) {
        this(position, 40.0, 40.0);
    }
}
