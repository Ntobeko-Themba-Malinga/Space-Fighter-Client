package org.space_fighter_client.game.objects;

import org.space_fighter_client.game.Position;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class Bullet extends ImageView {
    private final int SIZE = 30;
    private final int SPEED = 15;
    private final double direction;
    private final Position END;
    private int x;
    private int y;

    public Bullet(Position start, Position end, double direction) {
        setX(start.getX());
        setY(start.getY());
        setFitWidth(SIZE);
        setFitHeight(SIZE);
        this.direction = direction;
        this.END = end;
        setImage(new Image(getClass().getResource("d_bullet.png").toExternalForm()));
        setRotate(direction);
        setXAndY();
    }

    public void setXAndY() {
        x = 0;
        y = 0;
        if (direction == 0) {
            x = SPEED;
        } else if (direction == 270.0) {
            y = -SPEED;
        } else if (direction == 180.0) {
            x = -SPEED;
        } else if (direction == 90.0) {
            y = SPEED;
        }
    }

    public boolean move(AnchorPane root, Position worldSize) {
        Bullet bullet = this;

        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long l) {
                if ((l - lastUpdate) >= 14_000_000) {
                    setX(getX() + x);
                    setY(getY() + y);
                }

                if (!(new Position(getX(), getY())).isIn(new Position(0, worldSize.getY()), new Position(worldSize.getX(), 0))) {
                    root.getChildren().remove(bullet);
                    stop();
                }

                if (END != null && ((getX() > END.getX() && x == SPEED) ||
                    (getX() < END.getX() && x == -SPEED) ||
                    (getY() > END.getY() && y == SPEED) ||
                    (getY() < END.getY() && y == -SPEED))) {
                    root.getChildren().remove(bullet);
                    stop();
                }
            }
        }.start();
        return true;
    }
}
