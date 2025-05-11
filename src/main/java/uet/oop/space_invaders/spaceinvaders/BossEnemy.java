package uet.oop.space_invaders.spaceinvaders;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;

/**
 * Skeleton for uet.oop.space_invaders.spaceinvaders.BossEnemy. Students must implement behavior
 * without viewing the original implementation.
 */
public class BossEnemy extends Enemy {

    // Health points of the boss
    private int health;

    // Hitbox dimensions
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    // Horizontal movement speed
    private double horizontalSpeed;

    private static final int SCREEN_WIDTH = 360;
    private static final int SCREEN_HEIGHT = 600;

    /**
     * Constructs a uet.oop.space_invaders.spaceinvaders.BossEnemy at the given coordinates.
     * @param x initial X position
     * @param y initial Y position
     */
    public BossEnemy(double x, double y) {
        super(x, y);
        // TODO: initialize health, speeds, and load resources
        this.width = WIDTH;
        this.height = HEIGHT;
        this.health = 20;
        this.velocityY = 0.5;
        this.horizontalSpeed = 2.0;
    }

    /**
     * Update boss's position and behavior each frame.
     */
    @Override
    public void update() {
        y += velocityY;
        x += horizontalSpeed;
        // TODO: implement vertical and horizontal movement
        if (x < WIDTH / 2 || x > SCREEN_WIDTH - WIDTH / 2) {
            horizontalSpeed *= -1; // doi huong
        }

        if (y > SCREEN_HEIGHT + HEIGHT) {
            setDead(true);
        }
    }

    /**
     * Inflicts damage to the boss.
     */
    public void takeDamage() {
        // TODO: decrement health, mark dead when <= 0
        health--;
        if (health <= 0) {
            setDead(true);
        }
    }

    /**
     * Boss fires bullets towards the player.
     * @param newObjects list to which new bullets are added
     */
    public void shoot(List<GameObject> newObjects) {
        // TODO: implement shooting logic (spawn uet.oop.space_invaders.spaceinvaders.EnemyBullet)
        EnemyBullet bullet1 = new EnemyBullet(x - 10, y + HEIGHT / 2);
        EnemyBullet bullet2 = new EnemyBullet(x + 10, y + HEIGHT / 2);
        newObjects.add(bullet1);
        newObjects.add(bullet2);
    }

    /**
     * Render the boss on the canvas.
     * @param gc graphics context
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.DARKRED);
        gc.fillRect(x - width / 2, y - height / 2, width, height);
        // TODO: draw boss sprite or placeholder
        gc.setFill(Color.GRAY);
        gc.fillRect(x - width / 2, y - height / 2 - 10, width, 5);
        gc.setFill(Color.LIMEGREEN);
        double healthBarWidth = ((double) health / 20) * width;
        gc.fillRect(x - width / 2, y - height / 2 - 10, healthBarWidth, 5);
    }

    //giup hitbox chinh xac hon
    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }
}
