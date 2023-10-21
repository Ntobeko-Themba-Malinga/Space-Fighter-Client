package org.space_fighter_client.game.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Asteroid extends Rectangle {
    private final int SIZE = 10;

    public Asteroid(double x, double y) {
        setX(x);
        setY(y);
        setFill(Color.BROWN);
        setWidth(SIZE);
        setHeight(SIZE);
    }
}
