package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.gameobjects.Ball;
import bricker.main.BrickerGameManager;

import java.util.Random;

/**
 * A concrete implementation of the {@link CollisionStrategy} that, upon collision,
 * creates and launches two extra ball objects (often referred to as 'pucks') into the game,
 * in addition to the standard destruction of the brick.
 */
public class ExtraBallCollisionStrategy extends BasicCollisionStrategy
        implements CollisionStrategy {

    private final GameObject originalBall;
    private final Renderable puckRenderable;
    private final Vector2 puckSize;
    private final Sound ballSound;
    private final GameObjectCollection gameObjects;

    /**
     * Constructs a new ExtraBallCollisionStrategy instance.
     *
     * @param brickerGameManager The main game manager instance.
     * @param originalBall The reference to the main ball object,
     *                     used to determine the speed of the new balls.
     * @param puckRenderable The renderable object (image) for the new extra balls.
     * @param puckSize The dimensions (size) of the new extra balls.
     * @param ballSound The sound to be played when the new extra balls collide.
     * @param gameObjects The collection of game objects, used to add the new extra balls to the game.
     */
    public ExtraBallCollisionStrategy(BrickerGameManager brickerGameManager,
                                      GameObject originalBall,
                                      Renderable puckRenderable,
                                      Vector2 puckSize,
                                      Sound ballSound,
                                      GameObjectCollection gameObjects) {
        super(brickerGameManager);

        this.originalBall = originalBall;
        this.puckRenderable = puckRenderable;
        this.puckSize = puckSize;
        this.ballSound = ballSound;
        this.gameObjects = gameObjects;
    }

    /**
     * Executes the extra ball behavior.
     * <p>
     * 1. Calls the super strategy's {@code onCollision} to destroy the brick.
     * 2. Creates and initializes two new {@link Ball} objects (pucks) at the brick's center.
     * 3. Assigns them a random direction (upward arc) and the same speed as the original ball.
     * 4. Adds the new balls to the game.
     * </p>
     *
     * @param thisObj The brick object on which the collision occurred.
     * @param otherObj The object that collided with the brick.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {

        super.onCollision(thisObj, otherObj);

        Vector2 center = thisObj.getCenter();
        Random random = new Random();

        for (int i = 0; i < 2; i++) {
            Vector2 topLeft = new Vector2(center.x() - puckSize.x() / 2f,
                    center.y() - puckSize.y() / 2f);

            Ball extraBall = new Ball(topLeft, puckSize, puckRenderable, ballSound);
            extraBall.setTag("PUCK");
            gameObjects.addGameObject(extraBall, Layer.DEFAULT);


            double angle = random.nextDouble() * Math.PI ;
            float speed = originalBall.getVelocity().magnitude();
            Vector2 velocity = new Vector2((float)Math.cos(angle) * speed,
                    -(float)Math.sin(angle) * speed);
            extraBall.setVelocity(velocity);
        }
    }
}