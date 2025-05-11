package uet.oop.space_invaders.spaceinvaders;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    protected void onPlayButtonClick(javafx.event.ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(fxmlLoader.load(), 360, 600));
        popupStage.setTitle("Gameplay");
        popupStage.setResizable(false);
        popupStage.getIcons().add(new Image(getClass().getResource("/player.jpg").toString()));
        popupStage.show();

        Stage currentStage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        currentStage.hide();
    }

    @FXML
    protected void onQuitButtonClick() {
        System.exit(0);
    }

    @FXML
    protected void onInstructionsButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("instructions-view.fxml"));
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(fxmlLoader.load(), 360, 600));
        popupStage.setTitle("Instructions");
        popupStage.setResizable(false);
        popupStage.getIcons().add(new Image(getClass().getResource("/player.jpg").toString()));
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }


}