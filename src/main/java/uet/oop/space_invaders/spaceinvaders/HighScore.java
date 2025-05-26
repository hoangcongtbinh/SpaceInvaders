package uet.oop.space_invaders.spaceinvaders;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class HighScore {
    @FXML protected Label p1; @FXML protected Label s1;
    @FXML protected Label p2; @FXML protected Label s2;
    @FXML protected Label p3; @FXML protected Label s3;
    @FXML protected Label p4; @FXML protected Label s4;
    @FXML protected Label p5; @FXML protected Label s5;

    private static Path path = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", "SpaceShooter", "score.txt");

    private void createFile(Path path) throws IOException {
        Files.createDirectories(path.getParent()); // tạo thư mục nếu chưa có
        List<String> lines = List.of(
                "Player 1,0",
                "Player 2,0",
                "Player 3,0",
                "Player 4,0",
                "Player 5,0"
        );

        Files.write(path, lines, StandardOpenOption.CREATE);
    }

    @FXML
    private void reset(javafx.event.ActionEvent event) throws IOException {
        Files.deleteIfExists(path);
        createFile(path);
        readScore();
    }

    private void readScore() throws IOException {
        if (!Files.exists(path)) {
            createFile(path);
        }

        List<String> lines = Files.readAllLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String[] line = lines.get(i).split(",");
            switch (i) {
                case 0 : p1.setText(line[0]); s1.setText(line[1]); break;
                case 1 : p2.setText(line[0]); s2.setText(line[1]); break;
                case 2 : p3.setText(line[0]); s3.setText(line[1]); break;
                case 3 : p4.setText(line[0]); s4.setText(line[1]); break;
                case 4 : p5.setText(line[0]); s5.setText(line[1]); break;
            }
        }
    }

    @FXML
    private void initialize() throws IOException {
        readScore();
    }

}
