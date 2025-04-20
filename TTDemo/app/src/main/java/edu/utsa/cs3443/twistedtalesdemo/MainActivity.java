package edu.utsa.cs3443.twistedtalesdemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity handles the visual novel interface, displaying scenes, characters, dialogue,
 * and managing user interactions such as tapping, choosing branches, or entering input.
 */
public class MainActivity extends AppCompatActivity {

    // UI
    private SceneManager sceneManager;
    private ImageView backgroundImageView, mainIcon, otherIcon;
    private TextView nameTextView, dialogueTextView;
    private Button choiceButton1, choiceButton2;
    private EditText userInputEditText;
    private Button submitAnswerButton;
    private RelativeLayout tapContainer;

    // Typewriter effect
    private Handler textHandler = new Handler();
    private Runnable typeRunnable;
    private boolean isTyping = false;
    private int currentCharIndex = 0;
    private long typingSpeed = 30; // Typing speed per character in ms
    private String fullTextToType = "";

    /**
     * Initializes the UI and starts the scene.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link UI components to Java
        backgroundImageView = findViewById(R.id.backgroundImageView);
        mainIcon = findViewById(R.id.mainIcon);
        otherIcon = findViewById(R.id.otherIcon);
        nameTextView = findViewById(R.id.nameTextView);
        dialogueTextView = findViewById(R.id.dialogueTextView);
        choiceButton1 = findViewById(R.id.choiceButton1);
        choiceButton2 = findViewById(R.id.choiceButton2);
        userInputEditText = findViewById(R.id.userInputEditText);
        submitAnswerButton = findViewById(R.id.submitAnswerButton);
        tapContainer = findViewById(R.id.tapContainer);

        // Initializes the scene manager to control scene flow
        sceneManager = new SceneManager(this);
        showCurrentLine();

        // User taps to progress scene or trigger choices
        tapContainer.setOnClickListener(v -> {
            SceneLine current = sceneManager.getCurrentLine();

            if (isTyping) {
                skipTypewriter(); // If still typing, finish it immediately
                return;
            }

            // Prevents advancing if the input field is active
            if (current != null && "A.06".equals(current.sceneId)) {
                return;
            }

            if (sceneManager.isChoicePoint()) {
                sceneManager.waitForChoice(); // Pauses until choice is made
                showChoices();
            } else {
                sceneManager.advanceScene(); // Goes to next line
                if (sceneManager.isAtEnd()) {
                    showEnding(); // End of story
                } else {
                    showCurrentLine(); // Shows next line
                }
            }
        });
    }

    /**
     * Shows the final message and disables further interaction.
     */
    private void showEnding() {
        nameTextView.setText("");
        dialogueTextView.setText("The End. Thanks for playing!");

        mainIcon.setVisibility(View.GONE);
        otherIcon.setVisibility(View.GONE);
        userInputEditText.setVisibility(View.GONE);
        submitAnswerButton.setVisibility(View.GONE);

        tapContainer.setOnClickListener(null); // Disable tap events
    }

    /**
     * Displays the current scene line: name, dialogue, and images.
     */
    private void showCurrentLine() {
        SceneLine line = sceneManager.getCurrentLine();
        if (line == null) return;

        nameTextView.setText(line.name);
        startTypewriterEffect(line.dialogue);

        setImage(backgroundImageView, line.background);
        setImage(mainIcon, line.mainIcon);
        setImage(otherIcon, line.otherIcon);

        // Flips the second character horizontally
        mainIcon.setScaleX(1f);
        otherIcon.setScaleX(-1f);

        // Shows input prompt for specific scene
        if ("A.06".equals(line.sceneId)) {
            showInputPrompt();
        } else {
            hideInputPrompt();
        }
    }

    /**
     * Starts a typewriter-style effect to display the given text gradually,
     * one character at a time inside the dialogueTextView.
     *
     * @param text The full text to display with the typewriter effect.
     */
    private void startTypewriterEffect(String text) {
        // Saves the full text to type
        fullTextToType = text;

        // Starts from the beginning of the string
        currentCharIndex = 0;

        // Flags to track whether typing is currently happening
        isTyping = true;

        // Clears any existing text in the TextView
        dialogueTextView.setText("");

        // Defines a new Runnable that will be executed repeatedly to start typing
        typeRunnable = new Runnable() {
            @Override
            public void run() {
                // If not finished typing all characters
                if (currentCharIndex < fullTextToType.length()) {
                    // Append the next character to the TextView
                    dialogueTextView.append(String.valueOf(fullTextToType.charAt(currentCharIndex)));

                    // Moves to the next character
                    currentCharIndex++;

                    // Re-posts the Runnable after a delay to continue the effect
                    // "this" refers to the current Runnable instance
                    textHandler.postDelayed(this, typingSpeed);
                } else {
                    // When all characters have been typed, this stops the typing effect
                    isTyping = false;
                }
            }
        };

        // Starts typing effect immediately
        textHandler.post(typeRunnable);
    }

    /**
     * If user taps mid-typing, the dialogue text is instantly completed.
     */
    private void skipTypewriter() {
        if (isTyping) {
            textHandler.removeCallbacks(typeRunnable);
            dialogueTextView.setText(fullTextToType);
            isTyping = false;
        }
    }

    /**
     * Shows a text input prompt and handles submission.
     */
    private void showInputPrompt() {
        userInputEditText.setVisibility(View.VISIBLE);
        submitAnswerButton.setVisibility(View.VISIBLE);

        Log.d("MainActivity", "Showing input prompt and submit button");

        submitAnswerButton.setOnClickListener(v -> {
            String input = userInputEditText.getText().toString().trim();
            if (input.equals("3")) {
                sceneManager.advanceScene(); // Valid answer → next scene
                hideInputPrompt();
                showCurrentLine();
            } else {
                dialogueTextView.setText("Hmm... That doesn't sound right. Try again...");
            }
        });
    }

    /**
     * Hides the input prompt and submit button.
     */
    private void hideInputPrompt() {
        userInputEditText.setVisibility(View.GONE);
        submitAnswerButton.setVisibility(View.GONE);
    }

    /**
     * Displays two choice buttons with branching logic.
     */
    private void showChoices() {
        choiceButton1.setText("Good Idea!");
        choiceButton2.setText("Terrible Idea!");
        choiceButton1.setVisibility(View.VISIBLE);
        choiceButton2.setVisibility(View.VISIBLE);

        choiceButton1.setOnClickListener(v -> {
            sceneManager.loadBranch("HAGBranch2.csv");
            hideChoices();
            showCurrentLine();
        });

        choiceButton2.setOnClickListener(v -> {
            Log.d("MainActivity", "Loading HAGBranch1.csv");
            sceneManager.loadBranch("HAGBranch1.csv");
            hideChoices();
            showCurrentLine();
        });
    }

    /**
     * Hides the choice buttons.
     */
    private void hideChoices() {
        choiceButton1.setVisibility(View.GONE);
        choiceButton2.setVisibility(View.GONE);
    }

    /**
     * Dynamically loads a drawable image into an ImageView by name.
     *
     * @param imageView The view to load into.
     * @param imageName The name of the image (without extension).
     */
    private void setImage(ImageView imageView, String imageName) {
        // If imageName is null or empty, it skip loading to avoid crashing.
        if (imageName == null || imageName.isEmpty()) return;
        // Removes file extension (Ex: .jpg) from the imageName string, if present.
        // This ensures that only the base name is used when looking up the resource.
        String baseName = imageName.replace(".jpg", "").replace(".png", "");
        // Looks up the resource ID of the image using its base name.
        // getIdentifier() takes the name, resource type ("drawable"), and app's package name.
        int resId = getResources().getIdentifier(baseName, "drawable", getPackageName());
        imageView.setImageResource(resId);
    }
}
