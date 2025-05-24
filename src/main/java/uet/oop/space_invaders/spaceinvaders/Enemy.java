package uet.oop.space_invaders.spaceinvaders;

import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
import javafx.scene.image.Image;

/**
 * Skeleton for uet.oop.space_invaders.spaceinvaders.Enemy. Students must implement movement, rendering,
 * and death state without viewing the original implementation.
 */
public class Enemy extends GameObject {

    // Hitbox dimensions
    protected static final int WIDTH = 45;
    protected static final int HEIGHT = 30;

    // Movement speed
    public static double SPEED = 1;


    // Enemy image
    private final Image ENEMY_IMAGE = new Image(getClass().getResource("/enemy.png").toString());

    public Enemy() {
        this(0, 0);
    }

    /**
     * Constructs an uet.oop.space_invaders.spaceinvaders.Enemy at the given coordinates.
     * @param x initial X position
     * @param y initial Y position
     */
    public Enemy(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        // TODO: load sprite if needed and initialize dead flag
        this.dead = false;
        this.velocityY = SPEED;
    }

    /**
     * Set X coordinate for Enemy, supporting EnemyPool.
     *
     * @param x coordinate.
     */
    void setX(int x) {
        this.x = x;
    }

    /**
     * Set Y coordinate for Enemy, supporting EnemyPool.
     *
     * @param y coordinate.
     */
    void setY(int y) {
        this.y = y;
    }

    /**
     * Updates enemy position each frame.
     */
    @Override
    public void update() {
        y += velocityY;
        // TODO: implement vertical movement by SPEED
        if (y>800 + HEIGHT) {
            dead = true;
        }
    }

    /**
     * Renders the enemy on the canvas.
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        // TODO: draw sprite or fallback shape (e.g., colored rectangle)
        gc.drawImage(ENEMY_IMAGE,x - width / 2, y - height / 2, width, height);
    }

    /**
     * Returns the current width of the enemy.
     * @return WIDTH
     */
    @Override
    public double getWidth() {
        // TODO: return width
        return WIDTH;
    }

    /**
     * Returns the current height of the enemy.
     * @return HEIGHT
     */
    @Override
    public double getHeight() {
        // TODO: return height
        return HEIGHT;
    }

    /**
     * Marks this enemy as dead (to be removed).
     * @param dead true if enemy should be removed
     */
    public void setDead(boolean dead) {
        // TODO: update dead flag
        this.dead = dead;
    }

    /**
     * Checks if this enemy is dead.
     * @return true if dead, false otherwise
     */
    @Override
    public boolean isDead() {
        // TODO: return dead flag
        return dead;
    }
}
