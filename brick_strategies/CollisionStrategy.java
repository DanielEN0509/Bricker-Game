package bricker.brick_strategies;

import danogl.GameObject;

/**
 * Defines the contract for all specific collision behaviors that a
 * {@link bricker.gameobjects.Brick} can have.
 * <p>
 * This interface is a core component of the Strategy design pattern, allowing the logic
 * of what happens when a brick is hit to be interchangeable and independent of the brick object itself.
 * </p>
 */
public interface CollisionStrategy {
    /**
     * Executes the specific behavior associated with this strategy when a collision occurs.
     * This method is typically responsible for:
     * <ul>
     * <li>Handling the destruction/removal of the brick (if applicable).</li>
     * <li>Implementing any special effects (e.g., power-ups, extra balls, explosions).</li>
     * </ul>
     *
     * @param thisObj The {@link bricker.gameobjects.Brick}
     *                object on which the collision occurred (the brick itself).
     * @param otherObj The {@link danogl.GameObject} that collided with the brick (e.g., the ball).
     */
    void onCollision(GameObject thisObj, GameObject otherObj);
    /**
     * Retrieves the number of distinct behaviors or effects bundled within this strategy.
     * This is useful for complex strategies (like a composite strategy) or for weighting
     * purposes during strategy selection.
     *
     * @return The count of behaviors contained in this strategy. Default is 1 for simple strategies.
     */
    default int getBehaviorCount(){
        return 1;
    }
}
