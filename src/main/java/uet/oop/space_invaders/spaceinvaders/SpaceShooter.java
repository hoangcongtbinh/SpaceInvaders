package uet.oop.space_invaders.spaceinvaders;

//import java.util.Random;
//import java.util.Set;

//import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
//import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

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
    @FXML
    protected Label scoreLabel;

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

        initEventHandlers(scene);
    }

    /**
     * <li> All of <code> Game Logics</code> and <code>States</code> have been moved to <code>GameController.java</code> to ensure clarity and expand ability. </li>
     * <li> <strong> Sorry for the inconvenience! </strong> </li>
     */

    // Game mechanics stubs

    private void spawnEnemy() {
        // TODO: implement enemy and boss spawn logic based on score

    }

    private void spawnPowerUp() {
        // TODO: implement power-up spawn logic

    }

    private void spawnEnemyBullet() {

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
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-over.fxml"));
            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(fxmlLoader.load(), 360, 600));
            popupStage.setTitle("Game Over");
            popupStage.setResizable(false);
            popupStage.getIcons().add(new Image(getClass().getResource("/player.jpg").toString()));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restartGame() {
        // TODO: reset gameObjects, lives, score and switch back to game scene
        score = 0;
        numLives = 3;
        gameRunning = true;
        spawnEnemy();  // Reinitialize enemies
        spawnPowerUp();
    }

    private void resetGame() {
        // TODO: stop game loop and call showLosingScreen
        gameRunning = false;
        showLosingScreen();
    }

    private void initEventHandlers(Scene scene) {
        // TODO: set OnKeyPressed and OnKeyReleased for movement and shooting
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
            } else if (event.getCode() == KeyCode.LEFT) {
            } else if (event.getCode() == KeyCode.SPACE) {
            }
        });

        scene.setOnKeyReleased(event -> {
        });

    }

    private Pane createMenu() {
        // TODO: build and return main menu pane with styled buttons
        Pane menuPane = new Pane();
        Button startButton = new Button("Start");
        Button highScoreButton = new Button("High Score");
        Button instructionsButton = new Button("Instructions");
        Button quitButton = new Button("Quit");

        startButton.setOnAction(e -> {
            try {
                startGame(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        instructionsButton.setOnAction(e -> {
            try {
                showInstructions();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        quitButton.setOnAction(event -> quit());

        menuPane.getChildren().addAll(startButton, highScoreButton, instructionsButton, quitButton);
        return menuPane;
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

    @FXML
    private void closeWindow(javafx.event.ActionEvent event) throws IOException {
        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    private void showTempMessage(String message, double x, double y, double duration) {
        // TODO: show temporary on-screen message for duration seconds
    }

    @FXML
    private void startGame(javafx.event.ActionEvent event) throws IOException {
        // TODO: set gameRunning to true and switch to game scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.setScene(new Scene(fxmlLoader.load(), 360, 600));

        gameRunning = true;
    }

    @FXML
    private void quit() {
        System.exit(0);
    }
}
