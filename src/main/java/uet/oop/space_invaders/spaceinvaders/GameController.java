package uet.oop.space_invaders.spaceinvaders;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GameController {
    private List<Enemy> enemies;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    @FXML
    private Canvas canvas;

    public GameController() {

    }

    @FXML
    public void initialize() {
        this.gc = canvas.getGraphicsContext2D();
        this.enemies = new ArrayList<>();
        enemies.add(new Enemy(100, 100));
        enemies.add(new Enemy(200, 100));
        start();
    }

    public void update() {
        for (Enemy enemy: enemies) {
            enemy.update();
        }
        enemies.removeIf(Enemy::isDead);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }

}
