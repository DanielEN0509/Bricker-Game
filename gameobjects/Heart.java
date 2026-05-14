package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.main.BrickerGameManager;

/**
 * Represents a Heart object in the Bricker game.
 * This class serves a dual purpose:
 * 1. As a **Falling Heart** (power-up) dropped by a brick, which restores
 * a life upon collision with the paddle.
 * 2. As a **UI Heart** used purely for display in the LifeDisplay area.
 */
public class Heart extends GameObject {

    private static final float GRAVITY = 150f;

    private final LifeCounter lifeCounter;
    private final LifeDisplay lifeDisplay;
    private final int maxLives;
    private final Vector2 windowDimensions;
    private final GameObjectCollection gameObjects;
    private final BrickerGameManager gameManager;
    private final boolean isFallingHeart;

    /**
     * Constructs a new **Falling Heart** (power-up). This heart falls down and restores a life
     * if caught by the paddle.
     *
     * @param topLeftCorner The top-left corner position of the object (in pixels).
     * @param dimensions The width and height of the object (in pixels).
     * @param renderable The renderable object representing the heart's appearance.
     * @param lifeCounter A counter tracking the player's current lives.
     * @param lifeDisplay The UI element responsible for displaying the lives.
     * @param maxLives The maximum number of lives the player can have.
     * @param windowDimensions The dimensions of the game window.
     * @param gameObjects The collection of game objects for adding/removing.
     * @param gameManager The main game manager instance.
     */
    public Heart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                 LifeCounter lifeCounter, LifeDisplay lifeDisplay, int maxLives,
                 Vector2 windowDimensions, GameObjectCollection gameObjects,
                 BrickerGameManager gameManager) {

        super(topLeftCorner, dimensions, renderable);
        this.lifeCounter = lifeCounter;
        this.lifeDisplay = lifeDisplay;
        this.maxLives = maxLives;
        this.windowDimensions = windowDimensions;
        this.gameObjects = gameObjects;
        this.gameManager = gameManager;
        this.isFallingHeart = true;

        this.setVelocity(new Vector2(0, GRAVITY));
    }
    /**
     * Constructs a new **UI Heart**. This heart is static and is used only for display
     * purposes within the {@link LifeDisplay} class.
     *
     * @param topLeftCorner The top-left corner position of the object (in pixels).
     * @param dimensions The width and height of the object (in pixels).
     * @param renderable The renderable object representing the heart's appearance.
     */
    public Heart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);

        this.lifeCounter = null;
        this.lifeDisplay = null;
        this.maxLives = 0;
        this.windowDimensions = null;
        this.gameObjects = null;
        this.gameManager = null;

        this.isFallingHeart = false;
    }

    /**
     * Handles collision events for the heart.
     * If the heart is a falling power-up and collides with the paddle, it increments the life counter
     * (up to the maximum limit) and removes itself from the game.
     *
     * @param other The GameObject with which the heart collided.
     * @param collision Information regarding the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (!isFallingHeart) return;
        if (gameManager.isPaddle(other)) {

            if (lifeCounter.value() < maxLives) {
                lifeCounter.increment();
                lifeDisplay.update();
            }

            gameObjects.removeGameObject(this, Layer.DEFAULT);
        }
    }

    /**
     * Called every frame to update the heart's state.
     * If the heart is a falling power-up and moves off the bottom of the screen, it is removed from the game.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        if (!isFallingHeart) return;
        super.update(deltaTime);

        if (lifeCounter != null && this.getTopLeftCorner().y() > windowDimensions.y()) {
            gameObjects.removeGameObject(this, Layer.DEFAULT);
        }
    }

    /**
     * Determines which objects the heart should collide with.
     * Only falling hearts should collide, and they should only collide with the main paddle.
     *
     * @param other The other GameObject to check for potential collision.
     * @return true if the heart is falling and the other object is the paddle, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (!isFallingHeart) return false;
        return gameManager.isPaddle(other);
        }

    /**
     * Makes the heart object visible by adding it to the UI layer.
     * This is typically used for UI hearts.
     *
     * @param gameObjects The collection of game objects.
     */
    public void show(GameObjectCollection gameObjects) {
        gameObjects.addGameObject(this, Layer.UI);

    }

    /**
     * Hides the heart object by removing it from the UI layer.
     * This is typically used for UI hearts when the player loses a life.
     *
     * @param gameObjects The collection of game objects.
     */
    public void hide(GameObjectCollection gameObjects) {
        gameObjects.removeGameObject(this, Layer.UI);
    }

}

