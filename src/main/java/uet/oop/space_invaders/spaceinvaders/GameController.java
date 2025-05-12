package uet.oop.space_invaders.spaceinvaders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;
import java.util.Set;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

public class GameController {
    public static final int ENEMY_COUNT = 15;
    public static final int POWERUP_COUNT = 3;

    public static final int SPAWN_INTERVAL = 60;
    public static final int POWERUP_INTERVAL = 120;
    public static final int BULLET_INTERVAL = 140;

    private int enemyCount = 2;
    private int powerupCount = 0;
    private int enemyBulletCount = 0;

    private List<GameObject> gameObjects;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private int interval = 0;

    private Player player;

    @FXML
    private Canvas canvas;

    @FXML
    private Label information;

    public GameController() {

    }

    @FXML
    public void initialize() {
        this.gc = canvas.getGraphicsContext2D();
        this.gameObjects = new ArrayList<>();
        this.player = new Player(canvas.getWidth() / 2, canvas.getHeight() - 50);
        gameObjects.add(player);
        gameObjects.add(new Enemy(100, 200));
        gameObjects.add(new Enemy(200, -20));
        start();
    }

    public void enemyCreate() {
        if (interval % SPAWN_INTERVAL == 0 && enemyCount < ENEMY_COUNT) {
                Random r = new Random();
                gameObjects.add(new Enemy(r.nextInt(360 - Enemy.WIDTH), r.nextInt(10)));
            enemyCount++;
        }
    }

    public void powerupCreate() {
        if (interval % POWERUP_INTERVAL == 0 && powerupCount < POWERUP_COUNT) {
            Random r = new Random();
            gameObjects.add(new PowerUp(r.nextInt(360 - PowerUp.WIDTH), r.nextInt(10)));
            powerupCount++;
        }

    }

    public void enemyBulletCreate() {
        if (interval % BULLET_INTERVAL == 0) {
            List<GameObject> bullets = new ArrayList<>();
            for (GameObject enemy: gameObjects) {
                if (enemy instanceof Enemy) {
                    EnemyBullet enemyBullet = new EnemyBullet(enemy.getX(), enemy.getY());
                    bullets.add(enemyBullet);
                    enemyBulletCount++;
                }
            }
            gameObjects.addAll(bullets);
        }
    }

    public void update() {
        /* Manage death state and object count */
        Iterator<GameObject> it = gameObjects.iterator();
        while (it.hasNext()) {
            GameObject obj = it.next();
            if (obj.isDead()) {
                if (obj instanceof Enemy) enemyCount--;
                else if (obj instanceof PowerUp) powerupCount--;
                else if (obj instanceof EnemyBullet) enemyBulletCount--;

                it.remove();
            }
        }
    }

    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                information.setText(String.format("Enemy: %d\nPowerup: %d\nEnemyBullet: %d", enemyCount, powerupCount, enemyBulletCount));
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Reset frame

                enemyCreate();
                enemyBulletCreate();
                powerupCreate();
                update();
                playerInput();
                playerMovement();
                playerCollision();

                if (player.isDead()) {
                    // Go to game over screen
                    gc.fillText("Game Over", canvas.getWidth() / 2, canvas.getHeight() / 2);
                    gameLoop.stop();
                }

                for (GameObject object: gameObjects) {
                    object.update();
                    object.render(gc);
                }

                interval++;
            }
        };
        gameLoop.start();
    }

    // Keyboard input
    @FXML
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    private void playerInput() {
        canvas.getScene().setOnKeyPressed(key -> {
            pressedKeys.add(key.getCode());
        });

        canvas.getScene().setOnKeyReleased(key -> {
            pressedKeys.remove(key.getCode());
        });
    }

    // Player movement
    private void playerMovement() {
        player.setMoveForward(pressedKeys.contains(KeyCode.W));
        player.setMoveLeft(pressedKeys.contains(KeyCode.A));
        player.setMoveBackward(pressedKeys.contains(KeyCode.S));
        player.setMoveRight(pressedKeys.contains(KeyCode.D));

        if (pressedKeys.contains(KeyCode.SPACE)) {
            player.shoot(gameObjects);
        }
    }

    // Collision
    public void playerCollision() {
        for (GameObject object : gameObjects) {
            if ((object instanceof Enemy || object instanceof EnemyBullet) && player.isColliding(object)) {
                player.setDead(true);
            } else if (object instanceof PowerUp && player.isColliding(object)) {
                
            }
        }
    }
}
