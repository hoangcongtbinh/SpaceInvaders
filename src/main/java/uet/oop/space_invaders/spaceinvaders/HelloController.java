package uet.oop.space_invaders.spaceinvaders;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onQuitButtonClick() {
        System.exit(0);
    }
}