package za.co.armandkamffer.mamba.Commands.Models.CommandMessages;

public class PlanningAddTicketMessage {
    public String identifier;
    public String description;

    public PlanningAddTicketMessage(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }
}