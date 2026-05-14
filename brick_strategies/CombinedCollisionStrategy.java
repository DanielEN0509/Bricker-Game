package bricker.brick_strategies;

import danogl.GameObject;

/**
 * A concrete implementation of the {@link CollisionStrategy} interface that acts as a
 * **Composite Strategy**.
 * <p>
 * This class combines two or more individual collision strategies (effects) into a single unit.
 * When the {@code onCollision} method is called on the CombinedStrategy, it executes the
 * {@code onCollision} method of all its contained strategies sequentially.
 * </p>
 */
public class CombinedCollisionStrategy implements CollisionStrategy{

    private final CollisionStrategy first;
    private final CollisionStrategy second;

    /**
     * Constructs a new CombinedCollisionStrategy instance by composing two existing strategies.
     *
     * @param first The primary collision strategy to be executed first.
     * @param second The secondary collision strategy to be executed second.
     */
    public CombinedCollisionStrategy(CollisionStrategy first, CollisionStrategy second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Executes the collision logic for both the {@code first} and {@code second} strategies sequentially.
     *
     * @param thisObj The brick object on which the collision occurred.
     * @param otherObj The object that collided with the brick (e.g., the ball).
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        first.onCollision(thisObj, otherObj);
        second.onCollision(thisObj, otherObj);


    }

    /**
     * Calculates the total number of individual collision behaviors contained within this composite strategy.
     * This is done by summing the behavior counts of the {@code first} and {@code second} strategies.
     * This allows for recursive counting if nested {@link CombinedCollisionStrategy} objects are used.
     *
     * @return The combined total count of behaviors.
     */
    @Override
    public int getBehaviorCount() {
        return first.getBehaviorCount() + second.getBehaviorCount();
    }
}
