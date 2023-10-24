package org.space_fighter_client.game.objects;

import org.space_fighter_client.game.Position;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bullet extends Rectangle {
    private final int SIZE = 30;
    private final double direction;
    private final Position END;
    private int x;
    private int y;

    public Bullet(Position start, Position end, double direction) {
        setX(start.getX());
        setY(start.getY());
        setWidth(SIZE);
        setHeight(SIZE);
        this.direction = direction;
        this.END = end;
        setFill(Color.YELLOW);
        setXAndY();
    }

    public void setXAndY() {
        x = 0;
        y = 0;
        if (direction == 0) {
            x = 1;
        } else if (direction == 90.0) {
            y = -1;
        } else if (direction == 180.0) {
            x = -1;
        } else if (direction == 270.0) {
            y = 1;
        }
    }

    public boolean move(AnchorPane root) {
        System.out.println(direction);
        Bullet bullet = this;

        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long l) {
                if ((l - lastUpdate) >= 28_000_000) {
                    setX(getX() + x);
                    setY(getY() + y);
                }

                if ((getX() > END.getX() && x == 1) ||
                    (getX() < END.getX() && x == -1) ||
                    (getY() > END.getY() && y == 1) ||
                    (getY() < END.getY() && y == -1)) {
                    root.getChildren().remove(bullet);
                    stop();
                }
            }
        }.start();
        return true;
    }
}
