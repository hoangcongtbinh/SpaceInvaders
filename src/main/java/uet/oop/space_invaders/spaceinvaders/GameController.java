package uet.oop.space_invaders.spaceinvaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class GameController {
    public int ENEMY_LIMIT = 10;
    public static final int POWERUP_LIMIT = 3;
    public static final int BULLET_LIMIT = 25;
    public int ENEMY_PER_LAP = 3;

    /* Depends on Screen Refresh Rate */
    public int SPAWN_INTERVAL = 120;
    public int POWERUP_INTERVAL = 480;
    public int BULLET_INTERVAL = 240;
    public int FIRE_INTERVAL = 7;
    public static final int EXPLOSION_EDGE = 60;

    public static final int NOTIFICATION_TIMEOUT = 120;

    protected int enemyCount = 2;
    protected int powerupCount = 0;
    protected int bulletCount = 0;

    protected List<GameObject> gameObjects;
    protected ObjectPool<Enemy> enemyPool;
    protected ObjectPool<Bullet> bulletPool;
    protected ObjectPool<EnemyBullet> enemyBulletPool;
    protected ObjectPool<PowerUp> powerUpPool;

    // gun soundEffect in Player.java
    protected AudioClip explosion = new AudioClip(getClass().getResource("/explosion.wav").toString());
    protected AudioClip reward = new AudioClip(getClass().getResource("/reward.wav").toString());
    protected AudioClip target = new AudioClip(getClass().getResource("/target.wav").toString());

    // Image Resources
    protected Image HEART = new Image(getClass().getResource("/heart.png").toString());
    protected Image BAD_HEART = new Image(getClass().getResource("/badheart.png").toString());
    protected Image EXPLOSION_IMAGE = new Image(getClass().getResource("/explosion.png").toString());

    protected GraphicsContext gc;
    protected AnimationTimer gameLoop;
    protected Random r;

    protected int time = 0;
    protected int lastTime = 0;
    protected int notifyShownTime = 0;

    protected Player player;
    protected int score = 0;
    protected int health = 3;
    protected int level = 1; // max is 5, a step is 2500

    @FXML protected Pane game;

    protected Parent pause_view;
    protected SpaceShooter main;

    @FXML protected Canvas canvas;

    @FXML protected Label information;

    @FXML protected Label autoPlay;

    @FXML protected Label notification;

    @FXML protected ImageView heart1;

    @FXML protected ImageView heart2;

    @FXML protected ImageView heart3;

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

    public void objectSpawn() {
        int enemyCreated = 0;
        while (time % SPAWN_INTERVAL == 0 && enemyCount < ENEMY_LIMIT && enemyCreated < ENEMY_PER_LAP) {
            Enemy enemy = enemyPool.get();
            enemy.x = r.nextInt(360 - Enemy.WIDTH);
            enemy.y = r.nextInt(10);
            gameObjects.add(enemy);
            enemyCreated++;
            enemyCount++;
        }

        if (time % POWERUP_INTERVAL == 0 && powerupCount < POWERUP_LIMIT) {
            PowerUp powerUp = powerUpPool.get();
            powerUp.x = r.nextInt(360 - PowerUp.WIDTH);
            powerUp.y = r.nextInt(10);
            gameObjects.add(powerUp);
            powerupCount++;
        }

        if (time % BULLET_INTERVAL == 0) {
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

        /* Manage notification */
        if (time - notifyShownTime > NOTIFICATION_TIMEOUT && notification.isVisible()) {
            notification.setVisible(false);
        }
    }

    public void start() {
        pushNotification("Press P to enable AI", "lightgreen");
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                information.setText(String.format("Score: %d\nLevel: %d", score, level));
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Reset frame

                objectSpawn();
                playerInput();
                update();
                objectCollision();

                levelManagement();

                if (player.isAutoPlay()) {
                    player.autoUpdate(gameObjects, gameObjects, bulletPool);
                    autoPlay.setVisible(true);
                } else {
                    autoPlay.setVisible(false);
                }

                for (GameObject object: gameObjects) {
                    object.update();
                    object.render(gc);
                }

                time++;
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

            if (pressedKeys.contains(KeyCode.SPACE) && bulletCount < BULLET_LIMIT && (time - lastTime > FIRE_INTERVAL)) {
                lastTime = time;
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
        System.out.printf("Score: %d\n", score);
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
        notifyShownTime = time;
        notification.setVisible(true);
        notification.setText(body);
        notification.setStyle("-fx-background-color: " + color + ";"
                + "-fx-background-radius: 20;");
    }

    // Health management
    public void setHealth(int health) {
        this.health = health;
        heart1.setImage((this.health >= 1)? HEART : BAD_HEART);
        heart2.setImage((this.health >= 2)? HEART : BAD_HEART);
        heart3.setImage((this.health == 3)? HEART : BAD_HEART);
    }

    // Collision
    @FXML
    public void objectCollision() {
        for (GameObject object : gameObjects) {
            // Player side
            if ((object instanceof Enemy && (player.isColliding(object) || object.isCollidingWithBottom(canvas.getHeight()) == true)) ||
                    (object instanceof EnemyBullet && player.isColliding(object) && health == 1)) {
                gameLoop.stop();
                explosion.play();
                player.setDead(true);

                gc.drawImage(EXPLOSION_IMAGE, player.x - EXPLOSION_EDGE / 2, player.y - EXPLOSION_EDGE / 2, EXPLOSION_EDGE, EXPLOSION_EDGE);
                setHealth(0);

                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(event -> showLosingScreen());
                delay.play();

                break;
            } else if (object instanceof PowerUp && player.isColliding(object)) {
                reward.play();
                ((PowerUp) object).setDead(true);
                int reward = r.nextInt(11);
                if (reward <= 6) {
                    pushNotification("Score +100", "lightgreen");
                    score += 100;
                } else if (reward <= 8) {
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
                setHealth(this.health - 1);
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

    public void levelManagement() {
        if (score > level * 2500) {
            level++;
//            if (level == 6) {
//                pushNotification(String.format("Boss is Coming!", level), "orange");
//                return;
//            }
            pushNotification(String.format("Level %d", level), "orange");
            ENEMY_LIMIT += 5;
            ENEMY_PER_LAP ++;
            SPAWN_INTERVAL -= 10;
            POWERUP_INTERVAL += 10;
            BULLET_INTERVAL -= 20;
        }
    }
}
