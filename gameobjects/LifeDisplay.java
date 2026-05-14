package bricker.gameobjects;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.collisions.Layer;
import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * Manages the visual display of the player's remaining lives in the game's UI.
 * This includes displaying a series of {@link Heart} objects (icons) and a text representation
 * of the life count, updating both based on the {@link LifeCounter} state.
 */
public class LifeDisplay {
    private static final float DEFAULT_TEXT_HEIGHT = 30f;
    private static final float DEFAULT_PADDING = 5f;
    private static final float DEFAULT_TEXT_OFFSET = 10f;
    private static final float DEFAULT_X_OFFSET = 5f;

    // שדות
    private final LifeCounter lifeCounter;
    private final Heart[] lifeHearts;
    private final GameObjectCollection gameObjects;
    private final TextRenderable livesText;

    /**
     * Constructs a new LifeDisplay instance, initializing the heart icons and the text display.
     * The hearts and text are positioned in the bottom-left corner of the window.
     *
     * @param counter The {@link LifeCounter} object tracking the player's lives.
     * @param maxLives The maximum number of lives the player can have (used for array size).
     * @param heartSize The dimensions (width and height) of a single heart icon.
     * @param windowDimensions The dimensions of the game window, used for positioning the UI elements.
     * @param heartImage The renderable image for the heart icon.
     * @param gameObjects The collection of game objects where the UI elements will be added (Layer.UI).
     */
    public LifeDisplay(LifeCounter counter, int maxLives, Vector2 heartSize,
                       Vector2 windowDimensions, Renderable heartImage,
                       GameObjectCollection gameObjects) {

        this.lifeCounter = counter;
        this.gameObjects = gameObjects;
        this.lifeHearts = new Heart[maxLives];

        float startX = DEFAULT_X_OFFSET;
        float startY = windowDimensions.y() - heartSize.y();


        for (int i = 0; i < maxLives; i++) {
            Vector2 pos = new Vector2(startX + i * (heartSize.x() + DEFAULT_PADDING), startY);
            Heart heart = new Heart(pos, heartSize, heartImage);
            lifeHearts[i] = heart;


            if (i < lifeCounter.value()) heart.show(gameObjects);
        }

        livesText = new TextRenderable(Integer.toString(lifeCounter.value()));
        livesText.setColor(Color.GREEN);
        Vector2 textPos = new Vector2(startX, startY - DEFAULT_TEXT_OFFSET - DEFAULT_TEXT_HEIGHT);
        GameObject livesTextObj = new GameObject(textPos, new Vector2(50,
                DEFAULT_TEXT_HEIGHT), livesText);
        gameObjects.addGameObject(livesTextObj, Layer.UI);
    }

    /**
     * Updates the visual display based on the current life count.
     * This method is called whenever the player gains or loses a life.
     *
     * <p>Actions performed:</p>
     * 1. **Heart Icons:** Shows or hides the heart icons to match the remaining life count.
     * 2. **Text Update:** Updates the displayed text to the current life count value.
     * 3. **Color Coding:** Changes the text color based on the life count
     * (Green for 3+, Yellow for 2, Red for 1).
     */
    public void update() {
        int remainingLives = lifeCounter.value();

        for (int i = 0; i < lifeHearts.length; i++) {
            if (i < remainingLives) lifeHearts[i].show(gameObjects);
            else lifeHearts[i].hide(gameObjects);
        }

        livesText.setString(Integer.toString(remainingLives));
        if (remainingLives >= 3) livesText.setColor(Color.GREEN);
        else if (remainingLives == 2) livesText.setColor(Color.YELLOW);
        else if (remainingLives == 1) livesText.setColor(Color.RED);
    }
}

