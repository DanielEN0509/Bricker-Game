package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.gameobjects.Heart;
import bricker.gameobjects.LifeCounter;
import bricker.gameobjects.LifeDisplay;
import bricker.main.BrickerGameManager;

/**
 * A concrete implementation of the {@link CollisionStrategy} that, upon collision,
 * generates a falling {@link Heart} object (a life power-up) at the brick's location.
 * The brick is destroyed as part of this process.
 * The heart is only generated if the player does not already have the maximum number of lives.
 */
public class ExtraHealthCollisionStrategy extends BasicCollisionStrategy implements CollisionStrategy {

    private static final float CENTER_OF_THE_BRICK = 0.5f;

    private final Renderable heartRenderable;
    private final Vector2 heartSize;
    private final LifeCounter lifeCounter;
    private final LifeDisplay lifeDisplay;
    private final int maxLives;
    private final Vector2 windowDimensions;
    private final GameObjectCollection gameObjects;
    private final BrickerGameManager gameManager;

    /**
     * Constructs a new ExtraHealthCollisionStrategy instance.
     *
     * @param gameManager The main game manager instance.
     * @param heartRenderable The renderable object (image) for the falling heart power-up.
     * @param heartSize The dimensions (size) of the falling heart power-up.
     * @param lifeCounter A counter tracking the player's current lives.
     * @param lifeDisplay The UI element responsible for displaying the lives.
     * @param maxLives The maximum number of lives the player can possess.
     * @param windowDimensions The dimensions of the game window.
     * @param gameObjects The collection of game objects, used to add the new heart to the game.
     */
    public ExtraHealthCollisionStrategy(BrickerGameManager gameManager,
                                        Renderable heartRenderable,
                                        Vector2 heartSize,
                                        LifeCounter lifeCounter,
                                        LifeDisplay lifeDisplay,
                                        int maxLives,
                                        Vector2 windowDimensions,
                                        GameObjectCollection gameObjects) {
        super(gameManager);
        this.gameManager = gameManager;
        this.heartRenderable = heartRenderable;
        this.heartSize = heartSize;
        this.lifeCounter = lifeCounter;
        this.lifeDisplay = lifeDisplay;
        this.maxLives = maxLives;
        this.windowDimensions = windowDimensions;
        this.gameObjects = gameObjects;
    }

    /**
     * Executes the extra health behavior.
     * <p>
     * 1. Calls the super strategy's {@code onCollision} to destroy the brick.
     * 2. Checks if the player has reached the maximum number of lives; if so, the heart is not dropped.
     * 3. Creates and initializes a new falling {@link Heart} power-up at the center of the brick.
     * 4. Adds the heart to the default layer of the game.
     * </p>
     *
     * @param brick The brick object on which the collision occurred.
     * @param other The object that collided with the brick.
     */
    @Override
    public void onCollision(GameObject brick, GameObject other) {
        if (!BrickerGameManager.BRICK_TAG.equals(brick.getTag())) return;

        super.onCollision(brick, other);

        if (lifeCounter.value() >= maxLives) return;

        Vector2 brickPosition = brick.getTopLeftCorner();
        Vector2 heartCenterPos = brickPosition.add(brick.getDimensions().mult(CENTER_OF_THE_BRICK));

        Vector2 heartTopLeft = new Vector2(
                heartCenterPos.x() - heartSize.x() / 2f,
                heartCenterPos.y() - heartSize.y() / 2f
        );

        Heart heart = new Heart(
                heartTopLeft,
                heartSize,
                heartRenderable,
                lifeCounter,
                lifeDisplay,
                maxLives,
                windowDimensions,
                gameObjects,
                gameManager
        );

        gameObjects.addGameObject(heart, Layer.DEFAULT);
    }
}