package uet.oop.space_invaders.spaceinvaders;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Skeleton for uet.oop.space_invaders.spaceinvaders.EnemyBullet. Students must implement movement,
 * rendering, and state management.
 */
public class EnemyBullet extends GameObject {

    // Dimensions of the enemy bullet
    public static final int WIDTH = 5;
    public static final int HEIGHT = 5;

    // Movement speed of the bullet
    private static final double SPEED = 3;

    // Flag indicating whether the bullet should be removed
    private boolean dead;

    /**
     * Constructs an uet.oop.space_invaders.spaceinvaders.EnemyBullet at the given position.
     * @param x initial X position
     * @param y initial Y position
     */
    public EnemyBullet(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        // TODO: initialize dead flag if needed
        this.velocityY = SPEED;
        this.dead = false;
    }

    /**
     * Updates bullet position each frame.
     */
    @Override
    public void update() {
        y += velocityY;
        // TODO: move bullet vertically by SPEED
        if (y>600 + HEIGHT) {
            dead = true;
        }
    }

    /**
     * Renders the bullet on the canvas.
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        // TODO: draw bullet (e.g., filled rectangle or sprite)
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(x - width / 2, y - height / 2, width, height);
    }

    /**
     * Returns the width of the bullet.
     * @return WIDTH
     */
    @Override
    public double getWidth() {
        // TODO: return width
        return WIDTH;
    }

    /**
     * Returns the height of the bullet.
     * @return HEIGHT
     */
    @Override
    public double getHeight() {
        // TODO: return height
        return HEIGHT;
    }

    /**
     * Marks this bullet as dead (to be removed).
     * @param dead true if bullet should be removed
     */
    public void setDead(boolean dead) {
        // TODO: update dead flag
        this.dead = dead;
    }

    /**
     * Checks if this bullet is dead.
     * @return true if dead, false otherwise
     */
    @Override
    public boolean isDead() {
        // TODO: return dead flag
        return dead;
    }
}