package uet.oop.space_invaders.spaceinvaders;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;
import java.util.random.RandomGenerator;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GameController {
    public static final int ENEMY_COUNT = 15;
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
        start();
    }

    public void update() {
        Random r = new Random();
        for (Enemy enemy: enemies) {
            enemy.update();
        }
        enemies.removeIf(Enemy::isDead);

        while(enemies.size() < ENEMY_COUNT) {
            Enemy enemy = new Enemy(r.nextInt(360), r.nextInt(50));
            enemies.add(enemy);
        }

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }
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
