package za.co.armandkamffer.mamba.Commands.Models;

import com.google.gson.JsonObject;

public class Command {
    String type;
    JsonObject message;

    public Command(String type, JsonObject message) {
        this.type = type;
        this.message = message;
    }
}