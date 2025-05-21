package uet.oop.space_invaders.spaceinvaders;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import kotlin._Assertions;

import java.util.List;

/**
 * Skeleton for uet.oop.space_invaders.spaceinvaders.Player. Students must implement movement, rendering,
 * shooting, and health/state management.
 */
public class Player extends GameObject {

    // Hitbox dimensions
    private static final int WIDTH = 20;
    private static final int HEIGHT = 40;

    // Movement speed
    private static final double SPEED = 5;

    // Movement flags
    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveForward;
    private boolean moveBackward;

    // Player image
    private final Image PLAYER_IMAGE = new Image(getClass().getResource("/player.png").toString());

    // Player health
    private int health;

    // AI settings
    private boolean autoPlay = false;
    private int fireCooldown = 0;

    // Sound effect
    private SoundEffect gun = new SoundEffect("/gun.wav");

    // Sound
    protected GameController game;

    /**
     * Constructs a uet.oop.space_invaders.spaceinvaders.Player at the given position.
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
        public double getWidth() {
            // TODO: return width
            return WIDTH;
        }

        /**
         * Returns the height of the player.
         */
        @Override
        public double getHeight() {
            // TODO: return height
            return HEIGHT;
        }

        /**
         * Returns current health of the player.
         */
        public int getHealth() {
            // TODO: return health
            return health;
        }

        /**
         * Sets player's health.
         */
        public void setHealth(int health) {
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

        x = Math.max(WIDTH / 2, Math.min(360 - WIDTH / 2, x));
        y = Math.max(HEIGHT / 2, Math.min(600 - HEIGHT / 2, y));
    }

    /**
     * Renders the player on the canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        // TODO: draw sprite or placeholder shape
        gc.drawImage(PLAYER_IMAGE, x - width / 2, y - height / 2, width, height);
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
     * AI.
     */
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    /**
     * Shoots a bullet from the player.
     */
    public void shoot(List<GameObject> newObjects, ObjectPool<Bullet> objectPool) {
        // TODO: create and add new uet.oop.space_invaders.spaceinvaders.Bullet at (x, y - HEIGHT/2)
        if (!this.isAutoPlay() && !game.muted) {
            gun.play();
        }
        Bullet bullet = objectPool.get();
        bullet.x = this.x;
        bullet.y = this.y - height / 2;
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

    /**
     * thoi gian hoi chieu.
     * @return
     */
    private boolean intervalReadyToFire() {
        if (fireCooldown == 0) {
            fireCooldown = 2;
            return true;
        }
        fireCooldown--;
        return false;
    }

    public void autoUpdate(List<GameObject> objects, List<GameObject> newObjects, ObjectPool<Bullet> objectPool) {
        if (!autoPlay) return;

        boolean dangerLeft = false, dangerRight = false;

        double closestEnemyX = -1;

        double closestDistY = Double.MAX_VALUE;

        for (GameObject obj : objects) {
            if (obj instanceof Enemy) {
                double distY = Math.abs(600 - obj.getY());
                if (distY < closestDistY) {
                    closestDistY = distY;
                    closestEnemyX = obj.getX();
                }
            }
        }

        // lui ve sau de ban
        this.setMoveBackward(!this.isCollidingWithBottom(600));

        // uu tien ne dan truoc
        for (GameObject obj : objects) {
            if (obj instanceof EnemyBullet) {
                double dx = obj.getX() - this.x;
                double dy = obj.getY() - this.y;
                if (Math.abs(dx) < 30 && dy > -50 && dy < 100) {
                    if (dx < 0) dangerLeft = true;
                    else dangerRight = true;
                }
            }
        }

        // di chuyen ve muc tieu
        if (dangerLeft && !dangerRight) {
            setMoveRight(true);
            setMoveLeft(false);
        } else if (dangerRight && !dangerLeft) {
            setMoveLeft(true);
            setMoveRight(false);
        } else if (closestEnemyX != -1) {
            // di chuyen de ban neu an toan
            if (Math.abs(closestEnemyX - this.x) < 5) {
                setMoveLeft(false);
                setMoveRight(false);
            } else if (closestEnemyX < this.x) {
                setMoveLeft(true);
                setMoveRight(false);
            } else {
                setMoveLeft(false);
                setMoveRight(true);
            }
        } else {
            setMoveLeft(false);
            setMoveRight(false);
        }

        // ban lien tuc
        if (intervalReadyToFire()) {
            shoot(newObjects, objectPool);
        }
    }
}
