package uet.oop.space_invaders.spaceinvaders;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Skeleton for uet.oop.space_invaders.spaceinvaders.PowerUp. Students must implement movement,
 * rendering, and state management.
 */
public class PowerUp extends GameObject {

    // Dimensions of the power-up
    protected static final int WIDTH = 25;
    protected static final int HEIGHT = 25;

    // Fall speed of the power-up
    private static final double SPEED = 2;

    // Player image
    private final Image POWERUP_IMAGE = new Image(getClass().getResource("/powerup.png").toString());

    public PowerUp() {
        this(0, 0);
    }

    /**
     * Constructs a uet.oop.space_invaders.spaceinvaders.PowerUp at the given position.
     *
     * @param x initial X position
     * @param y initial Y position
     */
    public PowerUp(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        // TODO: initialize dead flag, load sprite if needed
        this.dead = false;
    }

    /**
     * Set X coordinate for PowerUp, supporting PowerUpPool.
     *
     * @param x coordinate.
     */
    void setX(double x) {
        this.x = x;
    }

    /**
     * Set Y coordinate for PowerUp, supporting PowerUpPool.
     *
     * @param y coordinate.
     */
    void setY(double y) {
        this.y = y;
    }


    /**
     * Updates power-up position each frame.
     */
    @Override
    public void update() {
        y+=SPEED;
        // TODO: move power-up vertically by SPEED
        if(y - height / 2 > 800) {
            dead = true;
        }
    }

    /**
     * Renders the power-up on the canvas.
     *
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        // TODO: draw sprite or fallback (e.g., colored rectangle)
        gc.drawImage(POWERUP_IMAGE, x - width / 2, y - height / 2, width, height);
    }

    /**
     * Returns the width of the power-up.
     *
     * @return WIDTH
     */
    @Override
    public double getWidth() {
        // TODO: return width
        return WIDTH;
    }

    /**
     * Returns the height of the power-up.
     *
     * @return HEIGHT
     */
    @Override
    public double getHeight() {
        // TODO: return height
        return HEIGHT;
    }

    /**
     * Checks whether the power-up should be removed.
     *
     * @return true if dead
     */
    @Override
    public boolean isDead() {
        // TODO: return dead flag
        return dead;
    }

    /**
     * Marks this power-up as dead (to be removed).
     *
     * @param dead true if should be removed
     */
    public void setDead(boolean dead) {
        // TODO: update dead flag
        this.dead = dead;
    }
}
