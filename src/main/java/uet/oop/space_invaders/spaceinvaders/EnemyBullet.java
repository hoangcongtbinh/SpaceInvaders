package uet.oop.space_invaders.spaceinvaders;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Skeleton for uet.oop.space_invaders.spaceinvaders.EnemyBullet. Students must implement movement,
 * rendering, and state management.
 */
public class EnemyBullet extends Bullet {

    // Dimensions of the enemy bullet
    protected static final double WIDTH = 3;
    protected static final double HEIGHT = 14;

    // Movement speed of the bullet
    private static final double SPEED = 3;


    public EnemyBullet() {
        this(0, 0);
    }

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
        if (y>800 + HEIGHT) {
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
}