package edu.utsa.cs3443.twistedtalesdemo;

import android.content.Context;
import java.util.List;

/**
 * SceneManager handles the current storyline of the visual novel.
 * It manages scene progression, branching logic, and keeps track
 * of the current dialogue line being displayed.
 */
public class SceneManager {

    // References the application context, used to load resources
    private Context context;

    // List holding the current scene's dialogue
    private List<SceneLine> currentScene;

    // Index of the current line being shown in the scene
    private int currentIndex = 0;

    // Flag indicating whether the app is waiting for a user choice before continuing
    private boolean waitingForChoice = false;

    // Flag indicating whether the story has branched from the neutral path
    private boolean hasBranched = false;

    /**
     * Constructor initializes the SceneManager and loads the default neutral scene.
     *
     * @param context The Android context used to access assets and resources.
     */
    public SceneManager(Context context) {
        this.context = context;
        loadNeutralScene();
    }

    /**
     * Retrieves the current dialogue line in the scene.
     *
     * @return The current SceneLine object, or null if the index is out of bounds.
     */
    public SceneLine getCurrentLine() {
        if (currentIndex < currentScene.size()) {
            return currentScene.get(currentIndex);
        }
        return null;
    }

    /**
     * Advances to the next line in the current scene,
     * unless the app is waiting for a player choice.
     */
    public void advanceScene() {
        if (!waitingForChoice) {
            currentIndex++;
        }
    }

    /**
     * Checks if the current scene is at a choice point
     *
     * @return true if the scene is at a decision point and hasn't branched yet.
     */
    public boolean isChoicePoint() {
        // Checks two conditions:
        // currentIndex == currentScene.size() - 1: on the last line of the current scene.
        // !hasBranched: ensures a branch is not selected again
        return currentIndex == currentScene.size() - 1 && !hasBranched;
    }

    /**
     * Checks whether the current scene has reached its end.
     *
     * @return true if all lines have been displayed.
     */
    public boolean isAtEnd() {
        return currentIndex >= currentScene.size();
    }

    /**
     * Loads the default neutral scene from "HAGNeutral.csv".
     * This is typically the starting scene.
     */
    public void loadNeutralScene() {
        currentScene = CsvLoader.loadScene(context, "HAGNeutral.csv");
        currentIndex = 0;
        waitingForChoice = false;
        hasBranched = false;
    }

    /**
     * Loads a specific scene branch based on the selected CSV file.
     * Resets the index and flags to begin the new scene.
     *
     * @param branchFile The filename of the branch to load (Ex: "HAGBranch1.csv").
     */
    public void loadBranch(String branchFile) {
        currentScene = CsvLoader.loadScene(context, branchFile);
        currentIndex = 0;
        waitingForChoice = false;
        hasBranched = true;  // Prevents returning to neutral after branching
    }

    /**
     * Sets the SceneManager to wait for a player choice.
     * This prevents advancing the scene until a choice is made.
     */
    public void waitForChoice() {
        waitingForChoice = true;
    }
}
