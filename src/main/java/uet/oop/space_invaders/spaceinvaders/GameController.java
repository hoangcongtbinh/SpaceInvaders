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
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

public class GameController {
    public static final int ENEMY_LIMIT = 15;
    public static final int POWERUP_LIMIT = 3;
    public static final int BULLET_LIMIT = 25;

    /* 1 interval = 1 second */
    public static final int SPAWN_INTERVAL = 3000;
    public static final int POWERUP_INTERVAL = 5500;
    public static final int BULLET_INTERVAL = 1500;
    public static int FIRE_INTERVAL = 200;
    public static double GAME_SPEED = 1.0; // 1.0x by default

    public static final int NOTIFICATION_TIMEOUT = 3000;

    private int enemyCount = 2;
    private int powerupCount = 0;
    private int bulletCount = 0;

    private List<GameObject> gameObjects;
    private ObjectPool<Enemy> enemyPool;
    private ObjectPool<Bullet> bulletPool;
    private ObjectPool<EnemyBullet> enemyBulletPool;
    private ObjectPool<PowerUp> powerUpPool;

    // gun soundEffect in Player.java
    private AudioClip explosion = new AudioClip(getClass().getResource("/explosion.wav").toString());
    private AudioClip reward = new AudioClip(getClass().getResource("/reward.wav").toString());
    private AudioClip target = new AudioClip(getClass().getResource("/target.wav").toString());

    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private Random r;

    private long time = 0;
    private int notifyShownTime = 0;

    private long lastSpawnTime;
    private long lastPowerUpTime;
    private long lastBulletTime;
    private long lastFireTime;
    private long lastUpdateTime;
    private long lastNotifyTime;

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
    private Label autoPlay;

    @FXML
    private Label notification;

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
        this.notification.setVisible(false);

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

        if (lastSpawnTime == 0) {
            lastSpawnTime = System.nanoTime();
            lastNotifyTime = System.nanoTime();
            lastBulletTime = System.nanoTime();
            lastFireTime = System.nanoTime();
            lastPowerUpTime = System.nanoTime();
            lastUpdateTime = System.nanoTime();
        }

        start();
    }

    public double tick(double lastTimeStamp) {
        return (System.nanoTime() - lastTimeStamp) / 1_000_000.0;
    }

    public void objectSpawn() {
        if (tick(lastSpawnTime) >= SPAWN_INTERVAL && enemyCount < ENEMY_LIMIT) {
            Enemy enemy = enemyPool.get();
            enemy.x = r.nextInt(360 - Enemy.WIDTH);
            enemy.y = r.nextInt(10);
            gameObjects.add(enemy);
            lastSpawnTime = time;
            enemyCount++;
        }

        if (tick(lastPowerUpTime) >= POWERUP_INTERVAL && powerupCount < POWERUP_LIMIT) {
            PowerUp powerUp = powerUpPool.get();
            powerUp.x = r.nextInt(360 - PowerUp.WIDTH);
            powerUp.y = r.nextInt(10);
            gameObjects.add(powerUp);
            lastPowerUpTime = time;
            powerupCount++;
        }

        if (tick(lastBulletTime) >= BULLET_INTERVAL) {
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
            lastBulletTime = time;
        }
    }

    public void update(long now) {
        /* Update animation by milisecond */
        if (tick(lastUpdateTime) >= 1.67) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Reset frame
            if (player.isAutoPlay()) {
                player.autoUpdate(gameObjects, gameObjects, bulletPool);
            }

            for (GameObject object: gameObjects) {
                object.update();
                object.render(gc);
            }
            lastUpdateTime = time;
        }

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

        /* Manage notification */
        if (tick(lastNotifyTime) > NOTIFICATION_TIMEOUT && notification.isVisible()) {
            notification.setVisible(false);
        }
    }

    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                information.setText(String.format("Score: %d", score));
                time = now;

                update(now);
                objectSpawn();
                playerInput();
                objectCollision();

                autoPlay.setVisible(player.isAutoPlay());
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

        if (pressedKeys.contains(KeyCode.P)) {
            player.setAutoPlay(!player.isAutoPlay());
            System.out.println("AI Mode: " + player.isAutoPlay());
        }
    }

    @FXML
    public void onKeyReleased(KeyEvent e) {
        pressedKeys.remove(e.getCode());
    }

    // Player movement
    public void playerInput() {
        if (!player.isAutoPlay()) {
            player.setMoveForward(pressedKeys.contains(KeyCode.W) || pressedKeys.contains(KeyCode.UP));
            player.setMoveLeft(pressedKeys.contains(KeyCode.A) || pressedKeys.contains(KeyCode.LEFT));
            player.setMoveBackward(pressedKeys.contains(KeyCode.S) || pressedKeys.contains(KeyCode.DOWN));
            player.setMoveRight(pressedKeys.contains(KeyCode.D) || pressedKeys.contains(KeyCode.RIGHT));

            if (pressedKeys.contains(KeyCode.SPACE) && bulletCount < BULLET_LIMIT && (tick(lastFireTime) >= FIRE_INTERVAL)) {
                lastFireTime = time;
                player.shoot(gameObjects, bulletPool);
                bulletCount++;
            }
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
            gameObjects.clear();

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

    // Push notification
    public void pushNotification(String body, String color) {
        lastNotifyTime = time;
        notification.setVisible(true);
        notification.setText(body);
        notification.setStyle("-fx-background-color: " + color + ";"
                + "-fx-background-radius: 20;");
    }

    // Health management
    public void setHealth(int health) {
        this.health = health;
        switch (this.health) {
            case 1:
                heart2.setImage(new Image(getClass().getResource("/badheart.png").toString()));
                heart3.setImage(new Image(getClass().getResource("/badheart.png").toString()));
            case 2:
                heart2.setImage(new Image(getClass().getResource("/heart.png").toString()));
                heart3.setImage(new Image(getClass().getResource("/badheart.png").toString()));
            case 3:
                heart2.setImage(new Image(getClass().getResource("/heart.png").toString()));
                heart3.setImage(new Image(getClass().getResource("/heart.png").toString()));
        }
    }

    // Collision
    @FXML
    public void objectCollision() {
        for (GameObject object : gameObjects) {
            // Player side
            if ((object instanceof Enemy && (player.isColliding(object) || object.isCollidingWithBottom(canvas.getHeight()) == true)) ||
                    (object instanceof EnemyBullet && player.isColliding(object) && health == 1)) {
                explosion.play();
                showLosingScreen();
            } else if (object instanceof PowerUp && player.isColliding(object)) {
                reward.play();
                ((PowerUp) object).setDead(true);
                int reward = r.nextInt(10);
                if (reward <= 7) {
                    pushNotification("Score +100", "lightgreen");
                    score += 100;
                } else if (reward <= 9) {
                    if (FIRE_INTERVAL > 3) {
                        FIRE_INTERVAL--;
                        pushNotification(String.format("Fire Rate +%.0f%%",
                                (1.0 / FIRE_INTERVAL) * 100), "lightgreen");
                    } else {
                        pushNotification("Fire Rate limit reached!", "orange");
                    }
                } else {
                    if (health < 3) {
                        setHealth(health + 1);
                        pushNotification(String.format("Health + 1",
                                (1 / FIRE_INTERVAL)), "lightgreen");
                    } else {
                        pushNotification(String.format("Health limit reached!",
                                (1 / FIRE_INTERVAL)), "orange");
                    }
                }

            } else if (object instanceof EnemyBullet && player.isColliding(object)) {
                target.play();
                ((EnemyBullet) object).setDead(true);
                setHealth(health - 1);
            }

            // Enemy side
            if (object instanceof Bullet) {
                for (GameObject enemy: gameObjects) {
                    if (enemy instanceof Enemy && enemy.isColliding(object)) {
                        target.play();
                        ((Enemy) enemy).setDead(true);
                        ((Bullet) object).setDead(true);
                        score += 50;
                    }
                }
            }
        }
    }
}
