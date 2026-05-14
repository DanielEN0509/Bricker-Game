package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;

import danogl.util.Vector2;
import bricker.main.BrickerGameManager;

/**
 * Represents a brick object in the Bricker game grid.
 * Each Brick holds its grid coordinates (row and column) and a specific
 * {@link brick_strategies.CollisionStrategy} that dictates its behavior
 * upon being hit by the ball (or another colliding object).
 * It also manages a counter for the total number of bricks remaining in the game.
 */
public class Brick extends  GameObject {

    private final int row;
    private final int col;
    private final CollisionStrategy collisionStrategy;

    /**
     * Constructs a new Brick instance.
     *
     * @param topLeftCorner The top-left corner position of the object (in pixels).
     * @param dimensions The width and height of the object (in pixels).
     * @param renderable The renderable object representing the brick's appearance.
     * @param row The row index of the brick in the game grid.
     * @param col The column index of the brick in the game grid.
     * @param collisionStrategy The strategy to be executed when the brick is hit.
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                 int row, int col, CollisionStrategy collisionStrategy) {
        super(topLeftCorner, dimensions, renderable);
        this.row = row;
        this.col = col;
        this.collisionStrategy = collisionStrategy;

        this.setTag(BrickerGameManager.BRICK_TAG);
    }

    /**
     * Called when a collision is detected between this brick and another GameObject.
     * The primary action of the brick upon collision is to delegate the destruction
     * and effect logic to its specific {@link CollisionStrategy}.
     * Note: The counter decrement is handled by the {@link bricker.brick_strategies.CollisionStrategy}
     * or the game manager to ensure it is counted only once, even if multiple effects occur.
     *
     * @param other The GameObject with which the brick collided (usually the ball).
     * @param collision Information regarding the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        collisionStrategy.onCollision(this, other);

    }

    /**
     * Retrieves the column index of the brick in the game grid.
     * @return The column index.
     */
    public int getCol() {
        return col;
    }

    /**
     * Retrieves the row index of the brick in the game grid.
     * @return The row index.
     */
    public int getRow() {
        return row;
    }
}
