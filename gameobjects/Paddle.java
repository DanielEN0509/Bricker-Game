package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.main.BrickerGameManager;

import java.awt.event.KeyEvent;

/**
 * Represents the player-controlled paddle in the Bricker game.
 * The Paddle object responds to user input (left/right arrow keys) to move horizontally
 * and is constrained by the left and right walls of the game window.
 */
public class Paddle extends GameObject {

    private final UserInputListener inputListener;
    private final WindowController windowController;
    private final float wallThickness;

    /**
     * Constructs a new Paddle instance.
     *
     * @param topLeftCorner The top-left corner position of the object (in pixels).
     * @param dimensions The width and height of the object (in pixels).
     * @param renderable The renderable object representing the paddle's appearance.
     * @param inputListener An object that provides information about the user's input (key presses).
     * @param windowController An object that provides access to window properties, like dimensions.
     * @param wallThickness The thickness of the side walls, used to define the paddle's movement boundaries.
     */
    public Paddle(Vector2 topLeftCorner,
                  Vector2 dimensions,
                  Renderable renderable,
                  UserInputListener inputListener,
                  WindowController windowController,
                  float wallThickness) {

        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowController = windowController;
        this.wallThickness = wallThickness;
    }

    /**
     * Called every frame to update the paddle's state, including movement and boundary checks.
     *
     * <p>The update performs the following actions:</p>
     * 1. **Reads Input:** Checks for LEFT and RIGHT arrow key presses.
     * 2. **Sets Velocity:** Updates the paddle's velocity based on the input and the predefined
     * {@link BrickerGameManager#PADDLE_SPEED}.
     * 3. **Applies Movement:** Calls {@code super.update(deltaTime)} to apply the calculated velocity.
     * 4. **Boundary Checks:** Ensures the paddle's movement is constrained by the left and right walls.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 movementDir = Vector2.ZERO;

        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        setVelocity(movementDir.mult(BrickerGameManager.PADDLE_SPEED));

        Vector2 currentTopLeft = getTopLeftCorner();
        float paddleWidth = getDimensions().x();
        float windowWidth = windowController.getWindowDimensions().x();
        // 4. Boundary Checks: Constrain movement based on wall thickness

        if (currentTopLeft.x() < wallThickness) {
            setTopLeftCorner(new Vector2(wallThickness, currentTopLeft.y()));
        }

        if (currentTopLeft.x() + paddleWidth > windowWidth - wallThickness) {
            setTopLeftCorner(new Vector2(windowWidth - paddleWidth - wallThickness,
                    currentTopLeft.y()));
        }



    }
}
