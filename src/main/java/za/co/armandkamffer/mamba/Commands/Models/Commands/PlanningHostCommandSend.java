package za.co.armandkamffer.mamba.Commands.Models.Commands;

import com.google.gson.JsonObject;

import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningHostCommandSendType;

public class PlanningHostCommandSend {
    PlanningHostCommandSendType type;
    JsonObject message;

    public PlanningHostCommandSend(PlanningHostCommandSendType type, JsonObject message) {
        this.type = type;
        this.message = message;
    }
}