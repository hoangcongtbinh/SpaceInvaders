package uet.oop.space_invaders.spaceinvaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MultiplayerGameController extends GameController {
    
    protected Player player2;

    @FXML protected ImageView heart1_player2;

    @FXML protected ImageView heart2_player2;

    @FXML protected ImageView heart3_player2;


    @Override
    public void initialize() throws IOException {
        // Initialize players
        this.gc = canvas.getGraphicsContext2D();
        this.gameObjects = new ArrayList<>();
        this.player = new Player(canvas.getWidth() / 3, canvas.getHeight() - 50);
        this.player2 = new Player(canvas.getWidth() * 2 / 3, canvas.getHeight() - 50, Player.PLAYER_BLUE_IMAGE);

        enemyPool = new ObjectPool<>(Enemy::new);
        bulletPool = new ObjectPool<>(Bullet::new);
        enemyBulletPool = new ObjectPool<>(EnemyBullet::new);
        powerUpPool = new ObjectPool<>(PowerUp::new);

        gameObjects.add(player);
        gameObjects.add(player2);
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

    @Override
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
                if (!player.isDead()) {
                    objectCollision(player);
                }
                if (!player2.isDead()) {
                    objectCollision(player2);
                }

                levelManagement();

                if (player.isAutoPlay()) {
                    player.autoUpdate(gameObjects, gameObjects, bulletPool);
                    autoPlay.setVisible(true);
                } else {
                    autoPlay.setVisible(false);
                }

                for (GameObject object : gameObjects) {
                    object.update();
                    object.render(gc);
                }

                time++;
            }
        };
        gameLoop.start();
    }

    @Override
    public void playerInput() {
        // Player 1 controls: WASD + SPACE
        // Player 2 controls: Arrow keys + ENTER

        if (!player.isAutoPlay()) {
            player.setMoveForward(pressedKeys.contains(KeyCode.W));
            player.setMoveBackward(pressedKeys.contains(KeyCode.S));
            player.setMoveLeft(pressedKeys.contains(KeyCode.A));
            player.setMoveRight(pressedKeys.contains(KeyCode.D));
            if (pressedKeys.contains(KeyCode.SPACE) && bulletCount < BULLET_LIMIT && (time - lastTime > FIRE_INTERVAL)) {
                lastTime = time;
                player.shoot(gameObjects, bulletPool);
                bulletCount++;
            }
        }
        
        if (!player2.isAutoPlay()) {
            player2.setMoveForward(pressedKeys.contains(KeyCode.UP));
            player2.setMoveBackward(pressedKeys.contains(KeyCode.DOWN));
            player2.setMoveLeft(pressedKeys.contains(KeyCode.LEFT));
            player2.setMoveRight(pressedKeys.contains(KeyCode.RIGHT));
            if (pressedKeys.contains(KeyCode.ENTER) && bulletCount < BULLET_LIMIT && (time - lastTime > FIRE_INTERVAL)) {
                lastTime = time;
                player2.shoot(gameObjects, bulletPool);
                bulletCount++;
            }
        }

        if (pressedKeys.contains(KeyCode.ESCAPE)) {
            showPausingScreen();
        }
    }

    @Override
    public void setHealth(int health, Player player) {
        if (player.equals(this.player)) {
            player.setHealth(health);
            heart1.setImage((player.health >= 1)? HEART : BAD_HEART);
            heart2.setImage((player.health >= 2)? HEART : BAD_HEART);
            heart3.setImage((player.health == 3)? HEART : BAD_HEART);
        } else if (player.equals(this.player2)) {
            player.setHealth(health);
            heart1_player2.setImage((player.health >= 1)? HEART : BAD_HEART);
            heart2_player2.setImage((player.health >= 2)? HEART : BAD_HEART);
            heart3_player2.setImage((player.health == 3)? HEART : BAD_HEART);
        } else {
            System.out.println("Invalid player");
        }
        System.out.println("Setting health for " + (player == this.player ? "player1" : "player2") + ": " + health);
    }

    @Override
    public void objectCollision(Player player) {
        for (GameObject object : gameObjects) {
            // Player side
            if ((object instanceof Enemy && (player.isColliding(object) || object.isCollidingWithBottom(canvas.getHeight()) == true)) ||
                    (object instanceof EnemyBullet && player.isColliding(object) && player.health == 1)) {
                player.setDead(true);
                gameObjects.remove(player);
                setHealth(0, player);
                
                explosion.play();
                gc.drawImage(EXPLOSION_IMAGE, player.x - EXPLOSION_EDGE / 2, player.y - EXPLOSION_EDGE / 2, EXPLOSION_EDGE, EXPLOSION_EDGE);

                if (this.player.isDead() && this.player2.isDead()) {
                    gameLoop.stop();
                    PauseTransition delay = new PauseTransition(Duration.seconds(3));
                    delay.setOnFinished(event -> showLosingScreen());
                    delay.play();
                }

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
                    if (player.health < 3) {
                        setHealth(health + 1, player);
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
                setHealth(player.health - 1, player);
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
