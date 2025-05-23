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

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class GameController {
    protected int ENEMY_LIMIT = 10;
    protected int POWERUP_LIMIT = 3;
    public static final int BULLET_LIMIT = 25;

    public static final int LEVEL_TIME = 5; // in seconds
    protected int ENEMY_PER_LAP = 3;

    /* Depends on Screen Refresh Rate */
    protected int SPAWN_INTERVAL = 120;
    protected int POWERUP_INTERVAL = 480;
    protected int LAST_POWERUP_INTERVAL = 120;
    protected int BULLET_INTERVAL = 240;
    protected int FIRE_INTERVAL = 7;

    public static final int EXPLOSION_EDGE = 60;

    public static final int NOTIFICATION_TIMEOUT = 240;

    protected int enemyCount = 2;
    protected int powerupCount = 0;
    protected int bulletCount = 0;

    protected List<GameObject> gameObjects;
    protected ObjectPool<Enemy> enemyPool;
    protected ObjectPool<Bullet> bulletPool;
    protected ObjectPool<EnemyBullet> enemyBulletPool;
    protected ObjectPool<PowerUp> powerUpPool;

    // gun soundEffect in Player.java
    protected SoundEffect explosion = new SoundEffect("/explosion.wav");
    protected SoundEffect reward = new SoundEffect("/reward.wav");
    protected SoundEffect target = new SoundEffect("/target.wav");
    protected SoundEffect boss = new SoundEffect("/boss.wav");
    protected SoundEffect win = new SoundEffect("/win.wav");

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
    protected long lastLevelTime = 0;

    public boolean muted = false;
    protected Player player;
    protected int score = 0;
    protected int health = 3;
    protected int level = 1; // max is 5, a step is 2500

    public static final int SINGLE_PLAYER = 1;
    public static final int MULTIPLAYER = 2;
    protected static int gameMode = 0;

    protected boolean isBossSpawned = false;
    protected boolean isBossCalled = false;
    protected int bossCount = 0;

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

    @FXML protected ImageView nosound;

    public GameController() {

    }

    @FXML
    public void initialize() throws IOException {
        this.gc = canvas.getGraphicsContext2D();
        this.gameObjects = new ArrayList<>();
        this.player = new Player(canvas.getWidth() / 2, canvas.getHeight() - 50);
        this.player.game = this;

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

        this.lastLevelTime = System.nanoTime();
        start();
    }

    // Push notification
    public void pushNotification(String body, String color) {
        notifyShownTime = time;
        notification.setVisible(true);
        notification.setText(body);
        notification.setStyle("-fx-background-color: " + color + ";"
                + "-fx-background-radius: 20;");
    }

    public void spawnBoss() {
        POWERUP_INTERVAL = LAST_POWERUP_INTERVAL - 30;
        POWERUP_LIMIT = 3;
        if (!muted) boss.play();
        pushNotification("Good luck!", "orange");
        gameObjects.add(new BossEnemy(90, 5));
        bossCount = 1;
        isBossSpawned = true;
    }

    public void objectSpawn() {
        if (time % POWERUP_INTERVAL == 0 && powerupCount < POWERUP_LIMIT) {
            PowerUp powerUp = powerUpPool.get();
            powerUp.x = r.nextInt(480 - PowerUp.WIDTH);
            powerUp.y = r.nextInt(10);
            gameObjects.add(powerUp);
            powerupCount++;
        }

        if (time % BULLET_INTERVAL == 0) {
            List<GameObject> bullets = new ArrayList<>();
            for (GameObject enemy: gameObjects) {
                if (enemy instanceof BossEnemy) {
                    ((BossEnemy) enemy).shoot(gameObjects);
                    break;
                } else if (enemy instanceof Enemy) {
                    EnemyBullet enemyBullet = enemyBulletPool.get();
                    enemyBullet.x = enemy.getX();
                    enemyBullet.y = enemy.getY();
                    bullets.add(enemyBullet);
                }
            }
            if (bullets.size() > 0) gameObjects.addAll(bullets);
        }

        if (level <= 5) {
            int enemyCreated = 0;
            while (time % SPAWN_INTERVAL == 0 && enemyCount < ENEMY_LIMIT && enemyCreated < ENEMY_PER_LAP) {
                Enemy enemy = enemyPool.get();
                enemy.x = r.nextInt(480 - Enemy.WIDTH);
                enemy.y = r.nextInt(10);
                gameObjects.add(enemy);
                enemyCreated++;
                enemyCount++;
            }
        } else if (!isBossSpawned && !isBossCalled) {
            pushNotification("Boss is Coming...", "orange");
            LAST_POWERUP_INTERVAL = POWERUP_INTERVAL;
            POWERUP_INTERVAL = 40;
            POWERUP_LIMIT = 8;
            PauseTransition delay = new PauseTransition(Duration.seconds(7));
            delay.setOnFinished(event -> spawnBoss());
            delay.play();
            isBossCalled = true;
        }
    }

    public void update() {
        /* Manage death state and object count */
        Iterator<GameObject> it = gameObjects.iterator();
        while (it.hasNext()) {
            GameObject obj = it.next();
            if (obj.isDead()) {
                if (obj instanceof BossEnemy) {
                    bossCount--;
                    score += 200;
                } else if (obj instanceof Enemy) {
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
                information.setText(String.format("Score: %d\nLevel: %s", score, (level <= 5) ? level : "BOSS"));
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Reset frame

                objectSpawn();
                playerInput();
                update();
                objectCollision();
                levelManagement();

                nosound.setVisible(muted);

                if (player.isAutoPlay()) {
                    player.autoUpdate(gameObjects, gameObjects, bulletPool);
                    autoPlay.setVisible(true);
                } else {
                    autoPlay.setVisible(false);
                }

                if (isBossSpawned && bossCount == 0) {
                    if (!muted) win.play();
                    PauseTransition delay = new PauseTransition(Duration.millis(300));
                    delay.setOnFinished(event -> showCongratsScreen());
                    delay.play();
                    gameLoop.stop();
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

        if (pressedKeys.contains(KeyCode.M)) {
            muted = !muted;
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

    public void showRegiScreen() throws IOException {
        if (RegiScore.isHighScore(this.score)) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("regiscore-view.fxml"));
            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(fxmlLoader.load(), 360, 200));
            popupStage.setTitle("High score");
            popupStage.setResizable(false);
            popupStage.getIcons().add(new Image(getClass().getResource("/player.jpg").toString()));

            RegiScore regiScore = fxmlLoader.getController();
            regiScore.score.setText(String.valueOf(this.score));

            popupStage.show();
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
            Scene scene = new Scene(fxmlLoader.load(), 480, 800);
            gameObjects.clear();

            SpaceShooter losingScreen = fxmlLoader.getController();
            losingScreen.scoreLabel.setText(String.format("Score: %d", score));

            currentStage.setScene(scene);
            showRegiScreen();

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

    public void showCongratsScreen() {
        System.gc();
        try {
            player.setDead(true);
            gameLoop.stop();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("win-view.fxml"));
            Stage currentStage = (Stage)(canvas).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 480, 800);
            gameObjects.clear();

            SpaceShooter winScreen = fxmlLoader.getController();
            winScreen.scoreLabel.setText(String.format("Score: %d", score));

            currentStage.setScene(scene);
            showRegiScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void returnGame() {
        game.getChildren().remove(pause_view);
        gameLoop.start();
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
                if (!muted) explosion.play();
                player.setDead(true);

                gc.drawImage(EXPLOSION_IMAGE, player.x - EXPLOSION_EDGE / 2, player.y - EXPLOSION_EDGE / 2, EXPLOSION_EDGE, EXPLOSION_EDGE);
                setHealth(0);

                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(event -> showLosingScreen());
                delay.play();

                break;
            } else if (object instanceof PowerUp && player.isColliding(object)) {
                if (!muted) reward.play();
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
                if (!muted) target.play();
                ((EnemyBullet) object).setDead(true);
                setHealth(this.health - 1);
            }

            // Enemy side
            if (object instanceof Bullet) {
                for (GameObject enemy: gameObjects) {
                    if (enemy instanceof BossEnemy && enemy.isColliding(object) && !object.isDead()){
                        if (!muted) target.play();
                        ((Bullet) object).setDead(true);
                        ((BossEnemy) enemy).takeDamage();
                        score += 50;
                    } else if (enemy instanceof Enemy && enemy.isColliding(object) && !object.isDead()) {
                        if (!muted) target.play();
                        ((Enemy) enemy).setDead(true);
                        ((Bullet) object).setDead(true);
                        score += 50;
                    }
                }
            }
        }
    }

    public void levelManagement() {
        if (System.nanoTime() - lastLevelTime >= LEVEL_TIME * Math.pow(10,9)) {
            level++;
            lastLevelTime = System.nanoTime();
            if (level >= 6) return;

            ENEMY_LIMIT += 5;
            ENEMY_PER_LAP ++;
            SPAWN_INTERVAL -= 10;
            POWERUP_INTERVAL += 10;
            BULLET_INTERVAL -= 20;
        }
    }
}
