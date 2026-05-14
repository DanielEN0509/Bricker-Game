package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.gameobjects.LifeCounter;
import bricker.gameobjects.LifeDisplay;
import bricker.main.BrickerGameManager;

import java.util.Map;
import java.util.Random;

/**
 * A Factory class responsible for creating and assigning specific {@link CollisionStrategy}
 * implementations to bricks based on a randomized range.
 * <p>
 * This class encapsulates the logic for initializing and combining various strategies,
 * ensuring each strategy receives the necessary dependencies (GameManager, assets, UI components).
 * </p>
 */
public class CollisionStrategyFactory {

    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;
    private final GameObject mainBall;
    private final float brickPadding;
    private final Map<GameObject, CollisionStrategy> strategyByBrick;

    // קבועי אובייקטים
    private final Renderable extraPaddleRenderable;
    private final Renderable extraBallRenderable;
    private final Renderable heartRenderable; // רנדר של הלב
    private final Sound ballSound;
    private final Vector2 smallBallSize;
    private final Vector2 paddleSize;
    private final Vector2 heartSize; // גודל הלב

    // תלויות חיים
    private final LifeCounter lifeCounter;
    private final int maxLives;
    private final LifeDisplay lifeDisplay;


    private final BrickerGameManager brickerGameManager;
    private final UserInputListener inputListener;
    private final WindowController windowController;


    private final Random random = new Random();

    /**
     * Constructs a new CollisionStrategyFactory and initializes all necessary dependencies
     * for creating various collision strategies.
     */
    public CollisionStrategyFactory(GameObjectCollection gameObjects,
                                    ImageReader imageReader,
                                    SoundReader soundReader,
                                    Vector2 windowDimensions,
                                    GameObject mainBall,
                                    float ballRadius,
                                    float paddleWidth,
                                    float paddleHeight,
                                    float brickPadding,
                                    Map<GameObject, CollisionStrategy> strategyByBrick,
                                    LifeCounter lifeCounter,
                                    int maxLives,
                                    LifeDisplay lifeDisplay,
                                    Renderable heartRenderable,
                                    Vector2 heartSize,
                                    BrickerGameManager brickerGameManager,
                                    UserInputListener inputListener,
                                    WindowController windowController) {

        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.mainBall = mainBall;
        this.brickPadding = brickPadding;
        this.strategyByBrick = strategyByBrick;
        this.brickerGameManager = brickerGameManager;

        // *** שמירת תלויות חיים ***
        this.lifeCounter = lifeCounter;
        this.maxLives = maxLives;
        this.lifeDisplay = lifeDisplay;
        this.heartRenderable = heartRenderable;
        this.heartSize = heartSize;


        // אתחול קבועי אובייקטים
        this.extraPaddleRenderable = imageReader.readImage("assets/paddle.png", true);
        this.extraBallRenderable = imageReader.readImage("assets/mockBall.png", true);
        this.ballSound = soundReader.readSound("assets/blop.wav");
        this.smallBallSize = new Vector2(ballRadius * 0.75f, ballRadius * 0.75f);
        this.inputListener = inputListener;
        this.windowController = windowController;
        this.paddleSize = new Vector2(paddleWidth, paddleHeight);
    }

    /**
     * Creates and returns a {@link CollisionStrategy} based on a random number
     * within the specified bounds. The range 1-5 usually results in a basic strategy,
     * while higher numbers result in special effect strategies or combined strategies.
     *
     * @param lowerBound The lower limit (inclusive) of the random selection range.
     * @param upperBound The upper limit (inclusive) of the random selection range.
     * @return A concrete implementation of CollisionStrategy.
     */
    public CollisionStrategy createStrategyByRange(int lowerBound, int upperBound) {
        int rangeSize = upperBound - lowerBound + 1;
        int randIndex = random.nextInt(rangeSize);
        int rand = randIndex + lowerBound;

        if (rand <= 5) {
            return new BasicCollisionStrategy(brickerGameManager);
        } else if (rand == 6) {
            return new ExtraHealthCollisionStrategy(
                    brickerGameManager,
                    this.heartRenderable,
                    this.heartSize,
                    this.lifeCounter,
                    this.lifeDisplay,
                    this.maxLives,
                    this.windowDimensions,
                    this.gameObjects);
        } else if (rand == 7) {
            return new ExtraBallCollisionStrategy(
                    brickerGameManager,
                    mainBall,
                    extraBallRenderable,
                    smallBallSize,
                    ballSound,
                    gameObjects
            );
        } else if (rand == 8) {
            return new ExtraPaddleCollisionStrategy(
                    gameObjects,
                    extraPaddleRenderable,
                    paddleSize,
                    windowDimensions,
                    brickerGameManager,
                    inputListener,
                    windowController
            );
        } else if (rand == 9) {
            return new ExplodingBrickCollisionStrategy(
                    brickerGameManager,
                    this.gameObjects,
                    this.brickPadding,
                    this.strategyByBrick);

        } else {
            return createDoubleBehaviorStrategy();
        }
    }

    /**
     * Creates a {@link CombinedCollisionStrategy} by bundling two or more special
     * collision effects together (e.g., Extra Health + Extra Ball).
     *
     * @return A {@link CombinedCollisionStrategy} object with multiple effects.
     */
    private CollisionStrategy createDoubleBehaviorStrategy() {
        CollisionStrategy strategy = createStrategyByRange(6, 10);

        int maxAdditions = 2;

        for (int i = 0; i < maxAdditions; i++) {
            if (strategy.getBehaviorCount() >= 3) break;

            CollisionStrategy additional = createStrategyByRange(6, 9);
            strategy = new CombinedCollisionStrategy(strategy, additional);
        }
        return strategy;
    }
}