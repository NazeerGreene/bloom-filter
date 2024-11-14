package learn.controller;

public class View {
    public void displayError(String msg) {
        String out = String.format("[ERROR] %s", msg);
        System.out.println(out);
    }
}
