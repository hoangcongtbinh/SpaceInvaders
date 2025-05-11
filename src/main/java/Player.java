
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Skeleton for Player. Students must implement movement, rendering,
 * shooting, and health/state management.
 */
public class Player extends GameObject {

    // Hitbox dimensions
    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;

    // Movement speed
    private static final double SPEED = 5;

    // Movement flags
    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveForward;
    private boolean moveBackward;

    // Player health
    private int health;

    // State flag for removal
    private boolean dead;

    /**
     * Constructs a Player at the given position.
     *
     * @param x initial X position
     * @param y initial Y position
     */
    public Player(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        // TODO: initialize health, dead flag, load sprite if needed
        this.health = 3;
        this.dead = false;
    }

        /**
         * Returns the width of the player.
         */
        @Override
        public double getWidth () {
            // TODO: return width
            return WIDTH;
        }

        /**
         * Returns the height of the player.
         */
        @Override
        public double getHeight () {
            // TODO: return height
            return HEIGHT;
        }

        /**
         * Returns current health of the player.
         */
        public int getHealth () {
            // TODO: return health
            return health;
        }

        /**
         * Sets player's health.
         */
        public void setHealth ( int health){
            // TODO: update health
            this.health = health;
            if (this.health <= 0) {
                dead = true;
            }
        }

    /**
     * Updates player position based on movement flags.
     */
    @Override
    public void update() {
        // TODO: implement movement with SPEED and screen bounds
        if (moveLeft) {
            x -= SPEED;
        }
        if (moveRight) {
            x += SPEED;
        }
        if (moveForward) {
            y -= SPEED;
        }
        if (moveBackward) {
            y += SPEED;
        }

        x = Math.max(WIDTH / 2, Math.min(600 - WIDTH / 2, x));
        y = Math.max(HEIGHT / 2, Math.min(360 - HEIGHT / 2, y));
    }

    /**
     * Renders the player on the canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        // TODO: draw sprite or placeholder shape
        gc.setFill(Color.GREEN);
        gc.fillRect(x - width / 2, y - height / 2, width, height);
    }

    /**
     * Sets movement flags.
     */
    public void setMoveLeft(boolean moveLeft) {
        // TODO: update moveLeft flag
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        // TODO: update moveRight flag
        this.moveRight = moveRight;
    }

    public void setMoveForward(boolean moveForward) {
        // TODO: update moveForward flag
        this.moveForward = moveForward;
    }

    public void setMoveBackward(boolean moveBackward) {
        // TODO: update moveBackward flag
        this.moveBackward = moveBackward;
    }

    /**
     * Shoots a bullet from the player.
     */
    public void shoot(List<GameObject> newObjects) {
        // TODO: create and add new Bullet at (x, y - HEIGHT/2)
        Bullet bullet = new Bullet(x,y - height/2);
        newObjects.add(bullet);
    }

    /**
     * Marks the player as dead.
     */
    public void setDead(boolean dead) {
        // TODO: update dead flag
        this.dead = dead;
    }

    /**
     * Checks whether the player is dead.
     */
    @Override
    public boolean isDead() {
        // TODO: return dead flag
        return dead;
    }
}
