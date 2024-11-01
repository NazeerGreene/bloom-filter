package learn;

import java.io.*;
import java.util.ArrayList;

public class Dictionary {
    private final String dictionaryFilePath;

    public Dictionary(String textfilePath) {
        this.dictionaryFilePath = textfilePath;
    }

    // method to get items from file
    public ArrayList<String> readAll() {
        ArrayList<String> elements = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFilePath))) {
            // no need to skip any line
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {

            }
        } catch (FileNotFoundException e) {
            // just return empty data if none found
        } catch (IOException e) {
            throw new RuntimeException("Could not open file: " + dictionaryFilePath, e);
        }

        return elements;
    }

        // method to get bit array from file

    // method to store bit array to file
}
