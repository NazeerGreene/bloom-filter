package learn;

import learn.hash.FNV1A64;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        double dsf = 0.01;
        int[] seeds = new int[]{1, 2, 3, 4, 5, 6};

        BloomFilter spellchecker = new BloomFilter(dsf, new FNV1A64(), seeds);

        boolean success = spellchecker.build(1000);

        ArrayList<String> elements = new ArrayList<>(List.of("hello", "world"));

        for (String element: elements) {
            spellchecker.add(element);
        }

        System.out.println("Querying for 'hello': " + spellchecker.contains("hello"));


    }

}
