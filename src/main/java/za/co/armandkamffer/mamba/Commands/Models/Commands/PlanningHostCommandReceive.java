package za.co.armandkamffer.mamba.Commands.Models.Commands;

import com.google.gson.JsonObject;

import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningHostCommandReceiveType;

public class PlanningHostCommandReceive {
    public PlanningHostCommandReceiveType type;
    public JsonObject message;

    public PlanningHostCommandReceive(PlanningHostCommandReceiveType type, JsonObject message) {
        this.type = type;
        this.message = message;
    }
}