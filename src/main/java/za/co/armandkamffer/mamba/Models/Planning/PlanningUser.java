package za.co.armandkamffer.mamba.Models.Planning;

import java.util.UUID;

public class PlanningUser {
    public UUID identifier;
    public String name;
    public String webSocketSessionId;

    public PlanningUser(String name, String webSocketSessionId) {
        this.name = name;
        identifier = UUID.randomUUID();
        this.webSocketSessionId = webSocketSessionId;
    }
}