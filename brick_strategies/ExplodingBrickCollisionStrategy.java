package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Vector2;
import bricker.main.BrickerGameManager;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

/**
 * A concrete implementation of the {@link CollisionStrategy} interface that causes
 * the brick to explode, destroying itself and all adjacent (up, down, left, right)
 * neighboring bricks.
 * <p>
 * This strategy uses an internal {@code destroyed} set to prevent infinite recursion
 * when a neighboring exploding brick's strategy is triggered.
 * </p>
 */
public class ExplodingBrickCollisionStrategy implements CollisionStrategy {

    private static final float EPS = 0.001f;
    private final BrickerGameManager brickerGameManager;
    private final GameObjectCollection gameObjects;
    private final float brickPadding;
    private final Map<GameObject, CollisionStrategy> strategyByBrick;
    private final Set<GameObject> destroyed = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Constructs a new ExplodingBrickCollisionStrategy instance.
     *
     * @param brickerGameManager The main game manager instance.
     * @param gameObjects The collection of game objects in the game.
     * @param brickPadding The padding space between bricks, necessary for accurate neighbor detection.
     * @param strategyByBrick A map linking GameObjects (bricks) to their respective strategies.
     * Used to identify bricks and trigger neighbor effects.
     */
    public ExplodingBrickCollisionStrategy(BrickerGameManager brickerGameManager,
                                           GameObjectCollection gameObjects,
                                           float brickPadding,
                                           Map<GameObject, CollisionStrategy> strategyByBrick) {
        this.brickerGameManager = brickerGameManager;
        this.gameObjects = gameObjects;
        this.brickPadding = brickPadding;
        this.strategyByBrick = strategyByBrick;
    }

    /**
     * Executes the exploding behavior.
     * <p>
     * 1. Adds the source brick to the {@code destroyed} set.
     * 2. Finds all adjacent neighboring bricks.
     * 3. For each neighbor:
     * a. Adds the neighbor to the {@code destroyed} set.
     * b. Triggers the neighbor's collision strategy (if not null) to apply any inherent effects.
     * c. Destroys the neighbor via the game manager.
     * 4. Destroys the source brick via the game manager.
     * </p>
     *
     * @param thisObj The brick object (the exploding brick).
     * @param otherObj The object that collided with the brick (e.g., the ball).
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        if (!strategyByBrick.containsKey(thisObj)) return;
        if (destroyed.contains(thisObj)) return;
        destroyed.add(thisObj);

        Vector2 originCenter = thisObj.getCenter();
        Vector2 originSize = thisObj.getDimensions();

        GameObject[] neighbors = findNeighbors(thisObj, originCenter, originSize);

        for (GameObject neighbor : neighbors) {
            if (neighbor == null || destroyed.contains(neighbor)) continue;
            destroyed.add(neighbor);

            CollisionStrategy strategy = strategyByBrick.get(neighbor);

            if (strategy != null) {
                    strategy.onCollision(neighbor, thisObj);
            }
            brickerGameManager.handleBrickDestruction(neighbor, Layer.STATIC_OBJECTS);
        }
        brickerGameManager.handleBrickDestruction(thisObj, Layer.STATIC_OBJECTS);
    }

    /**
     * Scans all static objects to find the immediate neighbors of the origin brick
     * (up, down, left, right), considering the brick padding.
     *
     * @param origin The brick to find neighbors for.
     * @param originCenter The center position of the origin brick.
     * @param originSize The dimensions of the origin brick.
     * @return An array containing up to four neighboring {@link GameObject}s that are also managed bricks.
     */
    private GameObject[] findNeighbors(GameObject origin, Vector2 originCenter, Vector2 originSize) {
        GameObject[] neighbors = new GameObject[4];
        int count = 0;

        for (GameObject go : gameObjects.objectsInLayer(Layer.STATIC_OBJECTS)) {
            if (go == origin ||
                    !strategyByBrick.containsKey(go)) continue;

            Vector2 neighborCenter = go.getCenter();
            boolean isAdjacent =
                    (floatEquals(neighborCenter.y(), originCenter.y()) &&
                            floatEquals(Math.abs(neighborCenter.x() - originCenter.x()),
                                    originSize.x() + brickPadding))
                            ||
                            (floatEquals(neighborCenter.x(), originCenter.x()) &&
                                    floatEquals(Math.abs(neighborCenter.y() - originCenter.y()),
                                            originSize.y() + brickPadding));

            if (isAdjacent && count < neighbors.length) {
                neighbors[count++] = go;
            }
        }
        return neighbors;
    }

    /**
     * Safely compares two floating-point numbers for approximate equality,
     * compensating for typical floating-point precision errors.
     *
     * @param a The first float value.
     * @param b The second float value.
     * @return true if the absolute difference between the values is less than {@link #EPS}.
     */
    private static boolean floatEquals(float a, float b) {
        return Math.abs(a - b) < EPS;
    }
}