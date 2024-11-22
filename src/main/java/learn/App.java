package learn;

import learn.controller.Controller;
import learn.dictionary.Read;
import learn.dictionary.Write;
import learn.hash.FNV1A64;
import learn.utils.BloomFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        try{
            Controller controller = new Controller();
            controller.run(new ArrayList<>(List.of(args)));
        } catch (IOException e) {
            System.out.println("Error while running application: " + e.getMessage());
        }
    }
}
