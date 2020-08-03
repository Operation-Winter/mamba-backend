package za.co.armandkamffer.mamba.Commands.Models.Commands;

import com.google.gson.JsonObject;

import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningJoinCommandReceiveType;

public class PlanningJoinCommandReceive {
    public PlanningJoinCommandReceiveType type;
    public JsonObject message;

    public PlanningJoinCommandReceive(PlanningJoinCommandReceiveType type, JsonObject message) {
        this.type = type;
        this.message = message;
    }
}