package za.co.armandkamffer.mamba.Commands.Models.Commands;

import com.google.gson.JsonObject;

import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningJoinCommandSendType;

public class PlanningJoinCommandSend {
    PlanningJoinCommandSendType type;
    JsonObject message;

    public PlanningJoinCommandSend(PlanningJoinCommandSendType type, JsonObject message) {
        this.type = type;
        this.message = message;
    }
}