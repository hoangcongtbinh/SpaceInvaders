package uet.oop.space_invaders.spaceinvaders;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Skeleton for SpaceShooter. Students must implement game loop,
 * spawning, collision checks, UI, and input handling.
 */
public class SpaceShooter extends Application {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 800;

    private GameController game;
    private HighScore highScore = new HighScore();

    // TODO: Declare UI labels, lists of GameObjects, player, root Pane, Scene, Stage

    public void setGame(GameController game) {
        this.game = game;
    }

    @FXML
    protected Label scoreLabel;

    @FXML protected Button ctrlbtn;
    @FXML protected Button rulesbtn;

    @FXML protected Pane rules;
    @FXML protected Pane controls;


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
        Scene scene = new Scene(fxmlLoader.load(), 480, 800);

        primaryStage.setResizable(false);
        Image icon = new Image(getClass().getResource("/player.jpg").toString());
        primaryStage.getIcons().add(icon);

        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * <li> All of <code> Game Logics</code> and <code>States</code> have been moved to <code>GameController.java</code> to ensure clarity and expand ability. </li>
     */

    @FXML
    private void showInstructions() throws IOException {
        // TODO: display instructions dialog
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("instructions-view.fxml"));
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(fxmlLoader.load(), 480, 800));
        popupStage.setTitle("Instructions");
        popupStage.setResizable(false);
        popupStage.getIcons().add(new Image(getClass().getResource("/player.jpg").toString()));
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    @FXML
    private void showScore() throws IOException {
        // TODO: display instructions dialog
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("highscore-view.fxml"));
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(fxmlLoader.load(), 360, 600));
        popupStage.setTitle("High score");
        popupStage.setResizable(false);
        popupStage.getIcons().add(new Image(getClass().getResource("/player.jpg").toString()));
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    @FXML
    private void closeWindow(javafx.event.ActionEvent event) throws IOException {
        System.gc();
        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    private void showTempMessage(String message, double x, double y, double duration) {
        // TODO: show temporary on-screen message for duration seconds
    }

    @FXML
    private void returnGame(ActionEvent event) {
        game.returnGame();
    }

    @FXML
    private void startGame(javafx.event.ActionEvent event) throws IOException {
        // TODO: set gameRunning to true and switch to game scene
        GameController.gameMode = GameController.SINGLE_PLAYER;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.setScene(new Scene(fxmlLoader.load(), 480, 800));
    }

    @FXML
    private void mainMenu(javafx.event.ActionEvent event) throws IOException {
        System.gc();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.setScene(new Scene(fxmlLoader.load(), 480, 800));
    }

    @FXML
    private void quit() {
        System.exit(0);
    }

    @FXML
    private void showGameModeSelection(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gamemode-view.fxml"));
        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.setScene(new Scene(fxmlLoader.load(), 480, 800));
    }

    @FXML
    private void startMultiplayerGame(javafx.event.ActionEvent event) throws IOException {
        GameController.gameMode = GameController.MULTIPLAYER;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("multiplayer-game-view.fxml"));
        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.setScene(new Scene(fxmlLoader.load(), 480, 800));
    }

    @FXML
    private void onRulesAction(javafx.event.ActionEvent event) throws IOException {
        controls.setVisible(false);
        rules.setVisible(true);

        ctrlbtn.setStyle("-fx-background-color: lightgray; -fx-background-radius: 20; -fx-border-radius: 20;");
        rulesbtn.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-radius: 20;");
    }

    @FXML
    private void onControlsAction(javafx.event.ActionEvent event) throws IOException {
        controls.setVisible(true);
        rules.setVisible(false);

        ctrlbtn.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-radius: 20;");
        rulesbtn.setStyle("-fx-background-color: lightgray; -fx-background-radius: 20; -fx-border-radius: 20;");
    }

    @FXML
    private void tryAgain(javafx.event.ActionEvent event) throws IOException {
        if (GameController.gameMode == GameController.SINGLE_PLAYER) {
            startGame(event);
        } else if (GameController.gameMode == GameController.MULTIPLAYER) {
            startMultiplayerGame(event);
        } else {
            System.out.println("Invalid game mode");
        }
    }
}
