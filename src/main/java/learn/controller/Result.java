package learn.controller;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private Status status;
    private ArrayList<String> messages;

    public Result(Status status) {
        this.status = status;
        messages = new ArrayList<>();
    }

    public void addMessage(String msg) {
        messages.add(msg);
    }

    public List<String> getMessages() {
        return this.messages;
    }
}
