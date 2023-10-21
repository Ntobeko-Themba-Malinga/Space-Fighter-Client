package org.space_fighter_client.game.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends Rectangle {
    private final int SIZE = 20;

    public Player(double x, double y) {
        setX(x);
        setY(y);
        setWidth(SIZE);
        setHeight(SIZE);
        setFill(Color.BLACK);
    }
}
