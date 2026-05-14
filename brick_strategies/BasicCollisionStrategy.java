package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.Layer;

import bricker.main.BrickerGameManager;

/**
 * A concrete implementation of the {@link CollisionStrategy} interface.
 * This strategy represents the default, most fundamental behavior for a brick:
 * when the brick is hit, it is simply destroyed and removed from the game.
 * It does not apply any special power-ups or effects.
 */
public class BasicCollisionStrategy implements CollisionStrategy {
    /**
     * The main game manager instance, required to call
     * the method responsible for handling brick destruction.
     */
    protected final BrickerGameManager brickerGameManager;

    /**
     * Constructs a new BasicCollisionStrategy instance.
     *
     * @param brickerGameManager The main game manager instance, required to call
     * the method responsible for handling brick destruction.
     */
    public BasicCollisionStrategy( BrickerGameManager brickerGameManager){

        this.brickerGameManager = brickerGameManager;
    }

    /**
     * Executes the basic collision behavior: destroys and removes the brick from the game.
     * This method calls the game manager's designated method to handle the destruction process.
     *
     * @param thisObj The brick object on which the collision occurred.
     * @param otherObj The object that collided with the brick (e.g., the ball).
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        brickerGameManager.handleBrickDestruction(thisObj, Layer.STATIC_OBJECTS);
    }
}

