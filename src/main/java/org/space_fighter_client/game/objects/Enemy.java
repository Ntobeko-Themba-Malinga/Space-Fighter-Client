package org.space_fighter_client.game.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Enemy extends Rectangle {
    private final int SIZE = 20;

    public Enemy(double x, double y) {
        setX(x);
        setY(y);
        setFill(Color.BLUE);
        setWidth(SIZE);
        setHeight(SIZE);
    }
}
