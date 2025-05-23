package uet.oop.space_invaders.spaceinvaders;

import javafx.fxml.FXML;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class RegiScore {
    @FXML TextField username;
    @FXML TextField score;

    private static Path path = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", "SpaceShooter", "score.txt");

    @FXML
    private void initialize() {
        username.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 20) return null;
            if (!newText.matches("[a-zA-Z0-9 _-]*")) return null;
            return change;
        }));
    }

    private static void createFile(Path path) throws IOException {
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
    private void setHighScore() throws IOException {
        if (!Files.exists(path)) {
            createFile(path);
        }

        List<String> lines = Files.readAllLines(path);
        lines.add(String.format("%s,%d", username.getText(), Integer.parseInt(score.getText())));

        lines.sort((a, b) -> {
            int scoreA = Integer.parseInt(a.split(",")[1]);
            int scoreB = Integer.parseInt(b.split(",")[1]);
            return Integer.compare(scoreB, scoreA);
        });

        lines.remove(5);
        Files.write(path, lines);

        Stage currentStage = (Stage) username.getScene().getWindow();
        currentStage.close();

    }

    public static boolean isHighScore(int score) throws IOException {
        if (!Files.exists(path)) {
            createFile(path);
        }

        List<String> lines = Files.readAllLines(path);
        for (int i = 0; i < lines.size(); i++) {
            String[] highest = lines.get(i).split(",");
            if (score > Integer.parseInt(highest[1])) {
                return true;
            }
        }

        return false;
    }
}
