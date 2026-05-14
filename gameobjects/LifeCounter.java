package bricker.gameobjects;

/**
 * A utility class responsible for tracking and managing the player's remaining lives.
 * This class provides methods to initialize, increment, decrement, and check the current life count.
 */
public class LifeCounter {
    private int lives;

    /**
     * Constructs a new LifeCounter instance, setting the initial number of lives.
     * * @param initialLives The starting number of lives for the player.
     */
    public LifeCounter(int initialLives) {
        this.lives = initialLives;
    }

    /**
     * Decrements the number of lives by one, provided the current life count is greater than zero.
     * This prevents the life count from becoming negative.
     */
    public void decrement() {

        if (lives > 0) {
            lives--;
        }
    }

    /**
     * Increments the number of lives by one.
     */
    public void increment() {
        lives++;
    }

    /**
     * Retrieves the current number of lives remaining.
     * * @return The current life count (an integer value).
     */
    public int value() {
        return lives;
    }

    /**
     * Checks if the player has at least one life remaining.
     * * @return {@code true} if the current life count is greater than zero; otherwise, {@code false}.
     */
    public boolean isAlive()
    {
        return lives > 0;
    }
}
