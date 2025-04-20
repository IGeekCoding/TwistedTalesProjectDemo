package edu.utsa.cs3443.twistedtalesdemo;

/**
 * Represents a single line in a scene of the visual novel.
 * Each SceneLine contains information about the current dialogue, characters, and background.
 */
public class SceneLine {

    public String sceneId;

    public String name;

    public String dialogue;

    public String mainIcon;

    public String otherIcon;

    public String background;

    /**
     * Constructor for creating a SceneLine object.
     *
     * @param sceneId    ID for the scene line, used to track and branch.
     * @param name       The name of the character speaking.
     * @param dialogue   The dialogue text to be displayed.
     * @param mainIcon   The primary character image to be displayed.
     * @param otherIcon  The secondary character image to be displayed.
     * @param background The background image to be to be displayed.
     */
    public SceneLine(String sceneId, String name, String dialogue, String mainIcon, String otherIcon, String background) {
        this.sceneId = sceneId;
        this.name = name;
        this.dialogue = dialogue;
        this.mainIcon = mainIcon;
        this.otherIcon = otherIcon;
        this.background = background;
    }
}
