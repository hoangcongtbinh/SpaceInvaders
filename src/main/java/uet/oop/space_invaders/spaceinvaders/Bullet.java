package uet.oop.space_invaders.spaceinvaders;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Skeleton for uet.oop.space_invaders.spaceinvaders.Bullet. Students must implement movement,
 * rendering, and state management.
 */
public class Bullet extends GameObject {

    // Width and height of the bullet
    public static final int WIDTH = 3;
    public static final int HEIGHT = 20;

    // Movement speed of the bullet
    private static final double SPEED = 7;

    // Who fire this bullet
    protected int player = 1;

    public Bullet() {
        this(0, 0);
    }

    /**
     * Constructs a uet.oop.space_invaders.spaceinvaders.Bullet at the given position.
     *
     * @param x initial X position
     * @param y initial Y position
     */
    public Bullet(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        // TODO: initialize dead flag if needed
        this.velocityY = -SPEED;
        this.dead = false;
    }

    /**
     * Constructs a uet.oop.space_invaders.spaceinvaders.Bullet at the given position.
     *
     * @param x initial X position
     * @param y initial Y position
     */
    public Bullet(double x, double y, int player) {
        super(x, y, WIDTH, HEIGHT);
        // TODO: initialize dead flag if needed
        this.velocityY = -SPEED;
        this.dead = false;
        this.player = player;
    }

    /**
     * Set X coordinate for Bullet, supporting BulletPool.
     *
     * @param x coordinate.
     */
    void setX(int x) {
        this.x = x;
    }

    /**
     * Set Y coordinate for Bullet, supporting BulletPool.
     *
     * @param y coordinate.
     */
    void setY(int y) {
        this.y = y;
    }


    /**
     * Updates bullet position each frame.
     */
    @Override
    public void update() {
        y += velocityY;
        // TODO: move bullet vertically by SPEED
        if (y + HEIGHT < 0) {
            dead = true;
        }
    }

    /**
     * Renders the bullet on the canvas.
     *
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        // TODO: draw bullet (e.g., filled rectangle or sprite)
        gc.setFill(Color.WHITE);
        gc.fillRect(x - width / 2, y - height / 2, width, height);
    }

    /**
     * Returns current width of the bullet.
     *
     * @return WIDTH
     */
    @Override
    public double getWidth() {
        // TODO: return bullet width
        return WIDTH;
    }

    /**
     * Returns current height of the bullet.
     *
     * @return HEIGHT
     */
    @Override
    public double getHeight() {
        // TODO: return bullet height
        return HEIGHT;
    }

    /**
     * Marks this bullet as dead (to be removed).
     *
     * @param dead true if bullet should be removed
     */
    public void setDead(boolean dead) {
        // TODO: update dead flag
        this.dead = dead;
    }

    /**
     * Checks if this bullet is dead.
     *
     * @return true if dead, false otherwise
     */
    @Override
    public boolean isDead() {
        // TODO: return dead flag
        return dead;
    }
}