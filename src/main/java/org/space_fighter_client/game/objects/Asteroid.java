package org.space_fighter_client.game.objects;

import java.util.Random;

import org.space_fighter_client.game.Position;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Asteroid extends ImageView {
    private final int SIZE = 40;

    public Asteroid(Position position) {
        setX(position.getX());
        setY(position.getY());
        setFitWidth(SIZE);
        setFitHeight(SIZE);
        Random random = new Random();
        String pic = "asteroid" + ((random.nextInt(3)) + 1) + ".png";
        setImage(new Image(getClass().getResource(pic).toExternalForm()));
    }
}
