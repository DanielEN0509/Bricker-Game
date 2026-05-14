package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.main.BrickerGameManager;

/**
 * Represents a temporary, extended paddle that appears as a power-up effect.
 * This paddle has a limited lifespan, measured by the number of times it collides with the ball,
 * and maintains a fixed vertical position.
 */
public class TemporaryPaddle extends Paddle {
    private final GameObjectCollection gameObjects;
    private static final int MAX_HIT = 4;
    private int hitsCount = 0;
    private final float fixedYPosition;
    private final BrickerGameManager gameManager;

    /**
     * Constructs a new TemporaryPaddle instance.
     *
     * @param topLeftCorner The top-left corner position of the object (in pixels).
     * @param dimensions The width and height of the object (in pixels).
     * @param renderable The renderable object representing the paddle's appearance.
     * @param inputListener An object that provides information about the user's input.
     * @param windowController An object that provides access to window properties.
     * @param gameObjects The collection of game objects for adding/removing.
     * @param gameManager The main game manager instance.
     * @param fixedYPosition The constant Y-coordinate where the paddle must remain.
     * @param wallThickness The thickness of the side walls, used for movement boundaries.
     */
    public TemporaryPaddle(Vector2 topLeftCorner,
                           Vector2 dimensions,
                           Renderable renderable,
                           UserInputListener inputListener,
                           WindowController windowController,
                           GameObjectCollection gameObjects,
                           BrickerGameManager gameManager,
                           float fixedYPosition,
                           float wallThickness) {

        super(topLeftCorner, dimensions, renderable,
                inputListener,
                windowController,
                wallThickness);

        this.gameObjects = gameObjects;
        this.fixedYPosition = fixedYPosition;
        this.gameManager = gameManager;
    }

    /**
     * Handles collision events for the TemporaryPaddle.
     * Increments the internal hit counter upon collision with any object that is NOT a brick or a wall.
     * When the hit count reaches {@link #MAX_HIT}, the temporary paddle is removed from the game.
     *
     * @param other The GameObject with which the paddle collided.
     * @param collision Information regarding the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (!gameManager.isBrick(other) && !gameManager.isWall(other)) {
            this.hitsCount++;

            if (this.hitsCount >= MAX_HIT) {
                gameObjects.removeGameObject(this, Layer.DEFAULT);
                gameManager.setExtraPaddle(null);

            }
        }
    }

    /**
     * Defines specific collision rules for the TemporaryPaddle.
     * The paddle must collide with the ball, but should explicitly ignore collisions with bricks and walls.
     *
     * @param other The other GameObject to check for potential collision.
     * @return true if collision should occur, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {

        if (other == gameManager.getBall()){
            return true;
        }

        if (gameManager.isBrick(other) || gameManager.isWall(other)) {
            return false;
        }

        return super.shouldCollideWith(other);
    }

    /**
     * Called every frame to update the paddle's state.
     * This method ensures the paddle's vertical position remains fixed to its designated Y-coordinate,
     * overriding any potential vertical movement from the base class.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 currentPos = this.getTopLeftCorner();
        this.setTopLeftCorner(new Vector2(currentPos.x(), fixedYPosition));
    }
}