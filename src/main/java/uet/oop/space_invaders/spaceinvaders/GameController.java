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
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GameController {
    public static final int ENEMY_COUNT = 15;
    public static final int POWERUP_COUNT = 3;

    public static final int SPAWN_INTERVAL = 60;
    public static final int POWERUP_INTERVAL = 30;

    private List<Enemy> enemies;
    private List<PowerUp> powerups;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private int interval = 0; // for enemies
    private int interval2 = 0; // for powerup

    @FXML
    private Canvas canvas;

    public GameController() {

    }

    @FXML
    public void initialize() {
        this.gc = canvas.getGraphicsContext2D();
        this.enemies = new ArrayList<>();
        this.powerups = new ArrayList<>();
        enemies.add(new Enemy(100, 200));
        enemies.add(new Enemy(200, -20));
        start();
    }

    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Reset frame

                for (Enemy enemy: enemies) {
                    enemy.update();
                    enemy.render(gc);
                }

                for (PowerUp powerup : powerups) {
                    powerup.update();
                    powerup.render(gc);
                }

                enemies.removeIf(Enemy::isDead);
                powerups.removeIf(PowerUp::isDead);

                if (enemies.size() < ENEMY_COUNT && interval == SPAWN_INTERVAL) {
                    Random r = new Random();
                    enemies.add(new Enemy(r.nextInt(360 - Enemy.WIDTH), r.nextInt(10)));
                    interval = 0;
                }

                if (powerups.size() < POWERUP_COUNT && interval2 == POWERUP_INTERVAL) {
                    Random r = new Random();
                    powerups.add(new PowerUp(r.nextInt(360 - PowerUp.WIDTH), r.nextInt(10)));
                    interval2 = 0;
                }

                interval++;
                interval2++;

            }
        };
        gameLoop.start();
    }

}
