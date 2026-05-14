package bricker.main;

import bricker.brick_strategies.*;
import bricker.brick_strategies.CollisionStrategy;
import bricker.gameobjects.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

/**
 * The main game manager for the Bricker game.
 * This class extends {@code danogl.GameManager} and is responsible for initializing the game
 * environment, creating all game objects (walls, paddle, ball, bricks, UI), running the game loop,
 * managing game state (lives, score), and handling win/loss conditions.
 */
public class BrickerGameManager extends GameManager {
    // --- Public Game Constants Documentation ---
    /** The thickness (width/height) of the boundary walls around the play area. */
    public static final float WALL_THICKNESS = 10f;
    /** The default width of the game window in pixels. */
    public static final int WINDOW_WIDTH = 700;
    /** The default height of the game window in pixels. */
    public static final float WINDOW_LENGTH = 500;
    /** The tag assigned to all brick GameObjects for identification purposes. */
    public static final String BRICK_TAG = "BRICK";
    /** The horizontal speed (units per second) applied to the main paddle. */
    public static final float PADDLE_SPEED = 350f;
    // --- Private/Static Game Constants ---
    private static final float BALL_SPEED = 250f;
    private static final float PADDLE_HEIGHT = 15f;
    private static final float PADDLE_WIDTH = 100f;
    private static final float BALL_RADIUS = 20f;
    private static final int DEFAULT_BRICKS_PER_ROW = 8;
    private static final int DEFAULT_NUMBER_OF_ROWS = 7;
    private static final float BRICK_HEIGHT = 15;
    private static final float BRICK_PADDING = 2;
    private static final int MAX_LIVES = 4;


    // --- Game State Fields ---
    private final int bricksPerRow;
    private final int numberOfRows;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private Counter bricksCounter;

    private bricker.gameobjects.Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private UserInputListener inputListener;

    private bricker.gameobjects.LifeCounter lifeCounter;
    private bricker.gameobjects.LifeDisplay lifeDisplay;

    // Map linking all bricks to their specific collision strategy
    private final Map<GameObject, CollisionStrategy> strategyByBrick = new IdentityHashMap<>();
    private CollisionStrategyFactory strategyFactory;
    private bricker.gameobjects.Paddle mainPaddle;
    private bricker.gameobjects.TemporaryPaddle extraPaddle; // The temporary wide paddle power-up

    /**
     * Constructs the BrickerGameManager.
     *
     * @param windowTitle The title displayed in the game window.
     * @param windowDimensions The width and height of the game window.
     * @param bricksPerRow The initial number of bricks per row.
     * @param numberOfRows The initial number of brick rows.
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions,
                              int bricksPerRow, int numberOfRows) {
        super(windowTitle, windowDimensions);
        this.bricksPerRow = bricksPerRow;
        this.numberOfRows = numberOfRows;
    }

    /**
     * Initializes the game environment, creating and positioning all initial game objects.
     * This method is called once at the start of the game or upon a game reset.
     */
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.inputListener = inputListener;
        lifeCounter = new LifeCounter(MAX_LIVES - 1);
        this.windowController = windowController;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        windowDimensions = windowController.getWindowDimensions();

        createBackground(imageReader);
        createWalls();
        createBall(imageReader, soundReader);
        createPaddle(imageReader, inputListener, windowController);

        bricksCounter = new Counter(bricksPerRow * numberOfRows);

        Vector2 heartSize = new Vector2(20, 20);
        Renderable heartImage = imageReader.readImage("assets/heart.png", true);
        lifeDisplay = new LifeDisplay(lifeCounter, MAX_LIVES, heartSize,
                windowDimensions, heartImage, gameObjects());

        strategyFactory = new CollisionStrategyFactory(
                gameObjects(), imageReader, soundReader, windowDimensions, ball, BALL_RADIUS,
                PADDLE_WIDTH, PADDLE_HEIGHT, BRICK_PADDING, strategyByBrick, lifeCounter, MAX_LIVES,
                lifeDisplay, heartImage, heartSize, this, inputListener, windowController

        );
        createBricks(imageReader);
    }

    /**
     * Ends the game with a win state and prompts the user to play again.
     */
    private void endGameWithWin() {
        boolean playAgain = windowController.openYesNoDialog("You win! Play again?");
        if (playAgain) {
            windowController.resetGame();
            extraPaddle = null;
        } else {
            windowController.closeWindow();
        }
    }

    /**
     * Clears the game scene and reinitialized the game state.
     */
    private void restartGame() {
        for (GameObject obj : gameObjects()) {
            gameObjects().removeGameObject(obj, Layer.DEFAULT);
        }
        for (GameObject obj : gameObjects().objectsInLayer(Layer.STATIC_OBJECTS)) {
            gameObjects().removeGameObject(obj, Layer.STATIC_OBJECTS);
        }
        for (GameObject obj : gameObjects().objectsInLayer(Layer.UI)) {
            gameObjects().removeGameObject(obj, Layer.UI);
        }

        extraPaddle = null;
        initializeGame(imageReader, soundReader, inputListener, windowController);
    }

    /**
     * The main game loop update method, called every frame.
     * Handles ball out-of-bounds, game over, and win condition checks.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (GameObject obj : gameObjects()) {
            if (obj != ball){
                if (obj.getTopLeftCorner().y() > windowDimensions.y() + 50) {
                    gameObjects().removeGameObject(obj, Layer.DEFAULT);
                }
            }
        }

        if (ball.getTopLeftCorner().y() > windowDimensions.y()) {
            lifeCounter.decrement();
            lifeDisplay.update();

            if (lifeCounter.isAlive()) {
                resetBall();
            } else {
                boolean playAgain = windowController.openYesNoDialog("You lose! Play again?");
                if (playAgain) {
                    restartGame();
                } else {
                    windowController.closeWindow();
                }
            }
        }
        if (bricksCounter.value() == 0 ||inputListener.isKeyPressed(KeyEvent.VK_W)) {
            endGameWithWin();
        }
    }

    /**
     * Creates and adds the main player paddle to the game.
     */
    private void createPaddle(ImageReader imageReader,
                              UserInputListener inputListener, WindowController windowController) {
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        mainPaddle = new Paddle(Vector2.ZERO, new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage, inputListener, windowController, WALL_THICKNESS);
        mainPaddle.setCenter(new Vector2(windowDimensions.x() / 2f, windowDimensions.y() - 30));
        gameObjects().addGameObject(mainPaddle);
    }

    /**
     * Checks if the given GameObject is the main player paddle.
     * @param obj The GameObject to check.
     * @return true if the object is the main paddle, false otherwise.
     */
    public boolean isPaddle(GameObject obj) {
        return obj == this.mainPaddle;
    }
    /**
     * Checks if the temporary wide paddle power-up is currently active.
     * @return true if {@code extraPaddle} is not null, false otherwise.
     */
    public boolean hasExtraPaddle() {
        return extraPaddle != null;
    }

    /**
     * Sets the active temporary paddle instance. Used when the power-up is created or removed.
     * @param paddle The {@link TemporaryPaddle} instance, or null if it's being removed.
     */
    public void setExtraPaddle(TemporaryPaddle paddle) {
        this.extraPaddle = paddle;
    }

    /**
     * Creates and adds the background image to the game.
     */
    private void createBackground(ImageReader imageReader) {
        Renderable bgImage = imageReader.readImage("assets/DARK_BG2_small.jpeg", false);
        GameObject background = new GameObject(Vector2.ZERO, windowDimensions, bgImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    /**
     * Creates and adds the boundary walls (left, right, and top) to the game.
     */
    private void createWalls() {
        // Left wall
        GameObject leftWall = new GameObject(
                Vector2.ZERO,
                new Vector2(WALL_THICKNESS, windowDimensions.y()),
                new RectangleRenderable(new Color(0,0,0,0))
        );
        gameObjects().addGameObject(leftWall, Layer.STATIC_OBJECTS);

        // Right wall
        GameObject rightWall = new GameObject(
                new Vector2(windowDimensions.x() - WALL_THICKNESS, 0),
                new Vector2(WALL_THICKNESS, windowDimensions.y()),
                new RectangleRenderable(new Color(0,0,0,0))
        );
        gameObjects().addGameObject(rightWall, Layer.STATIC_OBJECTS);

        // Top wall
        GameObject topWall = new GameObject(
                Vector2.ZERO,
                new Vector2(windowDimensions.x(), WALL_THICKNESS),
                new RectangleRenderable(Color.BLACK)
        );
        gameObjects().addGameObject(topWall, Layer.STATIC_OBJECTS);
    }

    /**
     * Checks if the given GameObject is one of the permanent boundary walls.
     * Note: This method iterates over the {@code Layer.STATIC_OBJECTS} which also contains bricks,
     * but the context of its use
     * (e.g., in {@link bricker.gameobjects.TemporaryPaddle}) often implies checking only the walls
     * if the object is not a brick.
     * @param obj The GameObject to check.
     * @return true if the object is found in the static objects layer, false otherwise.
     */
    public boolean isWall(GameObject obj) {
        for (GameObject go : gameObjects().objectsInLayer(Layer.STATIC_OBJECTS)) {
            if (go == obj) return true;
        }
        return false;
    }
    /**
     * Creates the grid of bricks, assigns a random collision strategy to each, and adds them to the game.
     */
    private void createBricks(ImageReader imageReader) {
        Renderable brickImage = imageReader.readImage("assets/brick.png", false);
        float totalPadding = (bricksPerRow - 1) * BRICK_PADDING;
        float brickWidth = (windowDimensions.x() - 2 * WALL_THICKNESS - totalPadding) / bricksPerRow;

        for (int row = 0; row < numberOfRows; row++) {
            for (int col = 0; col < bricksPerRow; col++) {
                CollisionStrategy strategy = strategyFactory.createStrategyByRange(1, 10);
                Brick brick = new Brick(
                        new Vector2(WALL_THICKNESS + col * (brickWidth + BRICK_PADDING),
                                WALL_THICKNESS + row * (BRICK_HEIGHT + BRICK_PADDING)),
                        new Vector2(brickWidth, BRICK_HEIGHT),
                        brickImage, row, col, strategy);
                gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
                strategyByBrick.put(brick, strategy);
            }
        }
    }

    /**
     * Checks if the given GameObject is a managed brick (i.e., exists in the strategy map).
     * @param obj The GameObject to check.
     * @return true if the object is a brick, false otherwise.
     */
    public boolean isBrick(GameObject obj) {
        return strategyByBrick.containsKey(obj);
    }

    /**
     * Creates and initializes the main ball object.
     */
    private void createBall(ImageReader imageReader, SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop.wav");
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADIUS, BALL_RADIUS), ballImage, collisionSound);
        resetBall();
        gameObjects().addGameObject(ball);
    }

    /**
     * Retrieves the main ball object.
     * @return The {@link bricker.gameobjects.Ball} instance.
     */
    public Ball getBall() {
        return this.ball;
    }

    /**
     * Resets the ball's position to the center of the window and gives it a new random initial velocity.
     */
    private void resetBall() {
        ball.setCenter(windowDimensions.mult(0.5f));
        Random rand = new Random();
        Vector2 initialVelocity;
        switch (rand.nextInt(4)) {
            case 0 -> initialVelocity = new Vector2(BALL_SPEED, BALL_SPEED);
            case 1 -> initialVelocity = new Vector2(-BALL_SPEED, BALL_SPEED);
            case 2 -> initialVelocity = new Vector2(BALL_SPEED, -BALL_SPEED);
            default -> initialVelocity = new Vector2(-BALL_SPEED, -BALL_SPEED);
        }
        ball.setVelocity(initialVelocity);
    }

    /**
     * Handles the final destruction and removal process for a brick.
     * This is called by the {@link CollisionStrategy} objects.
     *
     * @param brick The brick GameObject to destroy.
     * @param layer The layer the brick belongs to (typically {@code Layer.STATIC_OBJECTS}).
     */
    public void handleBrickDestruction(GameObject brick, int layer) {
        if (!"BRICK".equals(brick.getTag())) {
            return;
        }
        boolean isPresent = false;
        for (GameObject go : gameObjects().objectsInLayer(layer)) {
            if (go == brick) {
                isPresent = true;
                break;
            }
        }
        if (isPresent) {
            brick.setTag("DELETED");


            if (bricksCounter != null) {
                bricksCounter.decrement();
            }

            gameObjects().removeGameObject(brick, layer);
        }
    }

    /**
     * The main entry point for the application.
     * Allows passing optional command-line arguments for custom grid size.
     * @param args Optional arguments for bricksPerRow and numberOfRows.
     */
    public static void main(String[] args) {
        int bricksPerRow = DEFAULT_BRICKS_PER_ROW;
        int numberOfRows = DEFAULT_NUMBER_OF_ROWS;
        if (args.length == 2) {
            bricksPerRow = Integer.parseInt(args[0]);
            numberOfRows = Integer.parseInt(args[1]);
        }
        BrickerGameManager brickerGameManager = new BrickerGameManager("Bricker",
                new Vector2(WINDOW_WIDTH, WINDOW_LENGTH), bricksPerRow, numberOfRows);
        brickerGameManager.run();
    }
}
