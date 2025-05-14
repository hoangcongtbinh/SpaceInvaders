package uet.oop.space_invaders.spaceinvaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;
import java.util.Set;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

public class GameController {
    public static final int ENEMY_COUNT = 15;
    public static final int POWERUP_COUNT = 3;

    public static final int SPAWN_INTERVAL = 60;
    public static final int POWERUP_INTERVAL = 120;
    public static final int BULLET_INTERVAL = 140;
    public static final int FIRE_INTERVAL = 10;

    private int enemyCount = 2;
    private int powerupCount = 0;
    private int enemyBulletCount = 0;

    private List<GameObject> gameObjects;


    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private int interval = 0;
    private Player player;
    private int score = 0;
    private int health = 3;

    @FXML
    private Canvas canvas;

    @FXML
    private Label information;

    @FXML
    private ImageView heart2;

    @FXML
    private ImageView heart3;

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
                information.setText(String.format("Score: %d", score));
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Reset frame

                enemyCreate();
                enemyBulletCreate();
                powerupCreate();
                update();
                if (player.isAutoPlay()) {
                    player.autoUpdate(gameObjects, gameObjects);
                } else {
                    playerInput();
                    playerMovement();
                }
                objectCollision();
                System.gc();

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


            //Phim P de bat AI
            if (key.getCode() == KeyCode.P) {
                player.setAutoPlay(!player.isAutoPlay());
                System.out.println("AI Mode: " + player.isAutoPlay());
            }
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

        if (pressedKeys.contains(KeyCode.SPACE) && interval % FIRE_INTERVAL == 0) {
            player.shoot(gameObjects);
        }
    }

    private void showLosingScreen() {
        // TODO: display Game Over screen with score and buttons
        try {
            player.setDead(true);
            gameLoop.stop();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gameover-view.fxml"));
            Stage currentStage = (Stage)(canvas).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 360, 600);

            SpaceShooter losingScreen = fxmlLoader.getController();
            losingScreen.scoreLabel.setText(String.format("Score: %d", score));

            System.gc();
            currentStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Collision
    @FXML
    public void objectCollision() {
        for (GameObject object : gameObjects) {
            // Player side
            if ((object instanceof Enemy && (player.isColliding(object) || object.isCollidingWithBottom(canvas.getHeight()) == true)) ||
                    (object instanceof EnemyBullet && player.isColliding(object) && health == 1)) {
                    showLosingScreen();
            } else if (object instanceof PowerUp && player.isColliding(object)) {
                score += 100;
                ((PowerUp) object).setDead(true);
            } else if (object instanceof EnemyBullet && player.isColliding(object)) {
                ((EnemyBullet) object).setDead(true);
                health--;
                if (health == 2) heart3.setImage(new Image(getClass().getResource("/badheart.png").toString()));
                else if (health == 1) heart2.setImage(new Image(getClass().getResource("/badheart.png").toString()));
            }

            // Enemy side
            if (object instanceof Bullet) {
                for (GameObject enemy: gameObjects) {
                    if (enemy instanceof Enemy && enemy.isColliding(object)) {
                        ((Enemy) enemy).setDead(true);
                        ((Bullet) object).setDead(true);
                        score += 50;
                    }
                }
            }
        }
    }
}
