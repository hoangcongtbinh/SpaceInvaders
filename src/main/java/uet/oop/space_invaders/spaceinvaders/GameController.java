package uet.oop.space_invaders.spaceinvaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;
import java.util.Set;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

public class GameController {
    public static final int ENEMY_LIMIT = 15;
    public static final int POWERUP_LIMIT = 3;
    public static final int BULLET_LIMIT = 25;

    public static final int SPAWN_INTERVAL = 60;
    public static final int POWERUP_INTERVAL = 120;
    public static final int BULLET_INTERVAL = 140;
    public static final int FIRE_INTERVAL = 7;

    private int enemyCount = 2;
    private int powerupCount = 0;
    private int bulletCount = 0;

    private List<GameObject> gameObjects;
    private ObjectPool<Enemy> enemyPool;
    private ObjectPool<Bullet> bulletPool;
    private ObjectPool<EnemyBullet> enemyBulletPool;
    private ObjectPool<PowerUp> powerUpPool;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private Random r;

    private int interval = 0;
    private int lastInterval = 0;
    private Player player;
    private int score = 0;
    private int health = 3;

    @FXML
    private Pane game;
    private Parent pause_view;
    private SpaceShooter main;

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
    public void initialize() throws IOException {
        this.gc = canvas.getGraphicsContext2D();
        this.gameObjects = new ArrayList<>();
        this.player = new Player(canvas.getWidth() / 2, canvas.getHeight() - 50);

        enemyPool = new ObjectPool<>(Enemy::new);
        bulletPool = new ObjectPool<>(Bullet::new);
        enemyBulletPool = new ObjectPool<>(EnemyBullet::new);
        powerUpPool = new ObjectPool<>(PowerUp::new);

        gameObjects.add(player);
        gameObjects.add(new Enemy(100, 200));
        gameObjects.add(new Enemy(200, -20));
        this.r = new Random();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pause-view.fxml"));
            pause_view = fxmlLoader.load();
            main = fxmlLoader.getController();
            main.setGame(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    public void enemyCreate() {
        if (interval % SPAWN_INTERVAL == 0 && enemyCount < ENEMY_LIMIT) {
            Enemy enemy = enemyPool.get();
            enemy.x = r.nextInt(360 - Enemy.WIDTH);
            enemy.y = r.nextInt(10);
            gameObjects.add(enemy);
            enemyCount++;
        }
    }

    public void powerupCreate() {
        if (interval % POWERUP_INTERVAL == 0 && powerupCount < POWERUP_LIMIT) {
            PowerUp powerUp = powerUpPool.get();
            powerUp.x = r.nextInt(360 - PowerUp.WIDTH);
            powerUp.y = r.nextInt(10);
            gameObjects.add(powerUp);
            powerupCount++;
        }

    }

    public void enemyBulletCreate() {
        if (interval % BULLET_INTERVAL == 0) {
            List<GameObject> bullets = new ArrayList<>();
            for (GameObject enemy: gameObjects) {
                if (enemy instanceof Enemy) {
                    EnemyBullet enemyBullet = enemyBulletPool.get();
                    enemyBullet.x = enemy.getX();
                    enemyBullet.y = enemy.getY();
                    bullets.add(enemyBullet);
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
                if (obj instanceof Enemy) {
                    enemyCount--;
                    enemyPool.release((Enemy) obj);
                }
                else if (obj instanceof PowerUp) {
                    powerupCount--;
                    powerUpPool.release((PowerUp) obj);
                }
                else if (obj instanceof EnemyBullet) {
                    enemyBulletPool.release((EnemyBullet) obj);
                }
                else if (obj instanceof Bullet) {
                    bulletCount--;
                    bulletPool.release((Bullet) obj);
                }
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
                playerInput();
                objectCollision();

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
    public final Set<KeyCode> pressedKeys = new HashSet<>();

    @FXML
    public void onKeyPressed(KeyEvent e) {
        pressedKeys.add(e.getCode());
    }

    @FXML
    public void onKeyReleased(KeyEvent e) {
        pressedKeys.remove(e.getCode());
    }

    // Player movement
    public void playerInput() {
        player.setMoveForward(pressedKeys.contains(KeyCode.W));
        player.setMoveLeft(pressedKeys.contains(KeyCode.A));
        player.setMoveBackward(pressedKeys.contains(KeyCode.S));
        player.setMoveRight(pressedKeys.contains(KeyCode.D));

        if (pressedKeys.contains(KeyCode.SPACE) && bulletCount < BULLET_LIMIT && (interval - lastInterval > FIRE_INTERVAL)) {
            lastInterval = interval;
            player.shoot(gameObjects, bulletPool);
            bulletCount++;
        }

        if (pressedKeys.contains(KeyCode.ESCAPE)) {
            showPausingScreen();
        }
    }

    public void showLosingScreen() {
        System.gc();
        // TODO: display Game Over screen with score and buttons
        try {
            player.setDead(true);
            gameLoop.stop();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gameover-view.fxml"));
            Stage currentStage = (Stage)(canvas).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 360, 600);

            SpaceShooter losingScreen = fxmlLoader.getController();
            losingScreen.scoreLabel.setText(String.format("Score: %d", score));

            currentStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPausingScreen() {
        System.gc();
        gameLoop.stop();

        if (!game.getChildren().contains(pause_view)) {
            game.getChildren().add(pause_view);
        }
    }

    public void returnGame() {
        game.getChildren().remove(pause_view);
        gameLoop.start();
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
