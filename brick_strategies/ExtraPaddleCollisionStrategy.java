package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.gameobjects.TemporaryPaddle;
import bricker.main.BrickerGameManager;

/**
 * A concrete implementation of the {@link CollisionStrategy} interface that introduces
 * a temporary, second {@link bricker.gameobjects.TemporaryPaddle} (a "wide paddle" power-up)
 * into the game when the brick is hit.
 * The brick is destroyed as part of this process.
 * <p>
 * This effect can only be active once; if a temporary paddle is already present,
 * no new one is created.
 * </p>
 */
public class ExtraPaddleCollisionStrategy implements CollisionStrategy {

    private final GameObjectCollection gameObjects;
    private final Renderable paddleRenderable;
    private final Vector2 paddleSize;
    private final Vector2 windowDimensions;

    private final float fixedPaddleY;
    private final BrickerGameManager brickerGameManager;
    private final UserInputListener inputListener;
    private final WindowController windowController;

    /**
     * Constructs a new ExtraPaddleCollisionStrategy instance.
     *
     * @param gameObjects The collection of game objects, used to add the new paddle.
     * @param paddleRenderable The renderable object (image) for the temporary paddle.
     * @param paddleSize The dimensions (size) of the temporary paddle.
     * @param windowDimensions The dimensions of the game window.
     * @param brickerGameManager The main game manager instance.
     * @param inputListener An object providing access to user input.
     * @param windowController An object providing access to window properties.
     */
    public ExtraPaddleCollisionStrategy(GameObjectCollection gameObjects,
                                        Renderable paddleRenderable,
                                        Vector2 paddleSize,
                                        Vector2 windowDimensions,
                                        BrickerGameManager brickerGameManager,
                                        UserInputListener inputListener,
                                        WindowController windowController) {
        this.gameObjects = gameObjects;
        this.paddleRenderable = paddleRenderable;
        this.paddleSize = paddleSize;
        this.windowDimensions = windowDimensions;
        this.brickerGameManager = brickerGameManager;
        this.inputListener = inputListener;
        this.windowController = windowController;

        this.fixedPaddleY = windowDimensions.y() / 2f - paddleSize.y() / 2f;
    }

    /**
     * Executes the extra paddle behavior.
     * <p>
     * 1. Destroys the brick.
     * 2. Checks if a temporary paddle is already active using {@link BrickerGameManager#hasExtraPaddle()}.
     * 3. If no extra paddle is active, a new {@link TemporaryPaddle} is created at the center
     * of the screen's Y-axis and added to the game.
     * </p>
     *
     * @param thisObj The brick object on which the collision occurred.
     * @param otherObj The object that collided with the brick.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        brickerGameManager.handleBrickDestruction(thisObj, Layer.STATIC_OBJECTS);

        if (!brickerGameManager.hasExtraPaddle()) {
            Vector2 initialPos = new Vector2(
                    windowDimensions.x() / 2f - paddleSize.x() / 2f,
                    fixedPaddleY
            );

            TemporaryPaddle newPaddle = new TemporaryPaddle(initialPos,
                    paddleSize, paddleRenderable,
                    inputListener, windowController,
                    gameObjects, brickerGameManager,
                    fixedPaddleY,
                    BrickerGameManager.WALL_THICKNESS);

            gameObjects.addGameObject(newPaddle, Layer.DEFAULT);
            brickerGameManager.setExtraPaddle(newPaddle);
        }
    }
}