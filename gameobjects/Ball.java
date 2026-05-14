
package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents the main ball object in the Bricker game.
 * The Ball handles its own movement and reverses its velocity
 * direction upon collision with other game objects, playing a sound effect.
 */
public class Ball extends GameObject {

    private final Sound collisionSound;

    /**
     * Constructs a new Ball instance.
     *
     * @param topLeftCorner The top-left corner position of the object (in pixels).
     * @param dimensions The width and height of the object (in pixels).
     * @param renderable The renderable object representing the ball's appearance.
     * @param collisionSound The sound effect to be played when the ball collides with another object.
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;

    }
    /**
     * Called when a collision is detected between this ball and another GameObject.
     * Upon collision:
     * 1. Plays the collision sound.
     * 2. Reverses the velocity vector based on the collision normal, causing the ball to bounce.
     *
     * @param other The GameObject with which the ball collided.
     * @param collision Information regarding the collision (e.g., normal vector).
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        collisionSound.play();
        Vector2 newVal = getVelocity().flipped(collision.getNormal());
        setVelocity(newVal);

    }
}