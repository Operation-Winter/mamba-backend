package za.co.armandkamffer.mamba.Models.Planning;

import java.util.UUID;

import za.co.armandkamffer.mamba.Controllers.PlanningJoinWebSocketHandler;

public class User {
    public PlanningJoinWebSocketHandler webSocketHandler;
    public UUID identifier;
    public String name;

    public User(String name, PlanningJoinWebSocketHandler webSocketHandler) {
        this.name = name;
        this.webSocketHandler = webSocketHandler;
        identifier = UUID.randomUUID();
    }
}