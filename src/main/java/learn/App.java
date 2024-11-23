package learn;

import learn.controller.Controller;

import java.io.IOException;
import java.util.ArrayList;

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
