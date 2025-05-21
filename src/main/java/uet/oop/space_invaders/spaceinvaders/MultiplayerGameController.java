package uet.oop.space_invaders.spaceinvaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;

public class MultiplayerGameController extends GameController {
    
    protected Player player2;

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
                objectCollision(player);
                objectCollision(player2);

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
}
