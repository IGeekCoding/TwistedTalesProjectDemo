package edu.utsa.cs3443.twistedtalesdemo;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to load scene data from a CSV file located in the assets folder.
 * Each line of the CSV file is parsed into a SceneLine object.
 */
public class CsvLoader {

    /**
     * Loads a scene from a given CSV file in the assets folder.
     *
     * @param context  The context used to access application assets.
     * @param filename The name of the CSV file to load (e.g., "HAGNeutral.csv").
     * @return A list of SceneLine objects representing the scene.
     */
    public static List<SceneLine> loadScene(Context context, String filename) {
        List<SceneLine> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(filename)))) {

            // Reads and ignores the header line (column titles)
            String header = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                // Parses the line using a custom method that handles quoted CSV fields
                String[] parts = parseCsvLine(line);

                // Validates that we have the expected number of columns
                if (parts.length != 6) {
                    // Logs and skips lines with wrong format
                    Log.e("CsvLoader", "Skipping malformed line: " + line);
                } else {
                    Log.d("CsvLoader", "Parsed line: " + Arrays.toString(parts));
                    // Create a SceneLine object from the CSV parts and add it to the list
                    lines.add(new SceneLine(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }

        } catch (Exception e) {
            // Catches any file read or parsing exceptions and print the stack trace
            e.printStackTrace();
        }
        // Returns the complete list of parsed scene lines
        return lines;
    }

    /**
     * Parses a line of CSV text that may include quoted fields and commas inside quotes.
     *
     * @param line The CSV line as a string.
     * @return An array of strings, one for each CSV column.
     */
    private static String[] parseCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        // Iterates over each character in the line
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Handles quotation marks
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Handles escaped quote (Ex: "")
                    currentField.append('"');
                    i++; // Skips the next quote
                } else {
                    // Toggles the insideQuotes flag
                    insideQuotes = !insideQuotes;
                }
            } else if (c == ',' && !insideQuotes) {
                // If we're not inside quotes and hit a comma, end the current field
                parts.add(currentField.toString());
                currentField.setLength(0); // Clear the field buffer
            } else {
                // Regular character, adds to current field
                currentField.append(c);
            }
        }

        // Adds the last field after the loop ends
        parts.add(currentField.toString());

        // Returns all fields as an array
        return parts.toArray(new String[0]);
    }
}
