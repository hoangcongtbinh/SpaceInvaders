package uet.oop.space_invaders.spaceinvaders;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Skeleton for SpaceShooter. Students must implement game loop,
 * spawning, collision checks, UI, and input handling.
 */
public class SpaceShooter extends Application {

    public static final int WIDTH = 360;
    public static final int HEIGHT = 600;
    public static int numLives = 3;

    private int score;
    private boolean bossExists;
    private boolean reset;
    private boolean levelUpShown;
    private boolean gameRunning;

    // TODO: Declare UI labels, lists of GameObjects, player, root Pane, Scene, Stage

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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // TODO: initialize primaryStage, scene, canvas, UI labels, root pane
        // TODO: set up event handlers
        // TODO: initialize gameObjects list with player
        // TODO: create menu and switch to menu scene
        // TODO: set up AnimationTimer game loop and start it
        // TODO: show primaryStage
        FXMLLoader fxmlLoader = new FXMLLoader(SpaceShooter.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 600);

        primaryStage.setResizable(false);
        Image icon = new Image(getClass().getResource("/player.jpg").toString());
        primaryStage.getIcons().add(icon);

        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    // Game mechanics stubs

    private void spawnEnemy() {
        // TODO: implement enemy and boss spawn logic based on score
        if (interval % SPAWN_INTERVAL == 0 && enemyCount < ENEMY_COUNT) {
            Random r = new Random();
            gameObjects.add(new Enemy(r.nextInt(360 - Enemy.WIDTH), r.nextInt(10)));
            enemyCount++;
        }
    }

    private void spawnPowerUp() {
        // TODO: implement power-up spawn logic
        if (interval % POWERUP_INTERVAL == 0 && powerupCount < POWERUP_COUNT) {
            Random r = new Random();
            gameObjects.add(new PowerUp(r.nextInt(360 - PowerUp.WIDTH), r.nextInt(10)));
            powerupCount++;
        }
    }

    private void spawnBossEnemy() {
        // TODO: implement boss-only spawn logic
    }

    private void checkCollisions() {
        // TODO: detect and handle collisions between bullets, enemies, power-ups, player
    }

    private void checkEnemiesReachingBottom() {
        // TODO: handle enemies reaching bottom of screen (reduce lives, respawn, reset game)
    }

    // UI and game state methods

    private void showLosingScreen() {
        // TODO: display Game Over screen with score and buttons
    }

    private void restartGame() {
        // TODO: reset gameObjects, lives, score and switch back to game scene
    }

    private void resetGame() {
        // TODO: stop game loop and call showLosingScreen
    }

    private void initEventHandlers(Scene scene) {
        // TODO: set OnKeyPressed and OnKeyReleased for movement and shooting
    }

    private Pane createMenu() {
        // TODO: build and return main menu pane with styled buttons
        return new Pane();
    }

    @FXML
    private void showInstructions() throws IOException {
        // TODO: display instructions dialog
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("instructions-view.fxml"));
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(fxmlLoader.load(), 360, 600));
        popupStage.setTitle("Instructions");
        popupStage.setResizable(false);
        popupStage.getIcons().add(new Image(getClass().getResource("/player.jpg").toString()));
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    private void showTempMessage(String message, double x, double y, double duration) {
        // TODO: show temporary on-screen message for duration seconds
    }

    @FXML
    private void startGame(javafx.event.ActionEvent event) throws IOException {
        // TODO: set gameRunning to true and switch to game scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(fxmlLoader.load(), 360, 600));
        popupStage.setTitle("Gameplay");
        popupStage.setResizable(false);
        popupStage.getIcons().add(new Image(getClass().getResource("/player.jpg").toString()));
        popupStage.show();

        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.hide();

        gameRunning = true;
    }

    @FXML
    private void quit() {
        System.exit(0);
    }
}
