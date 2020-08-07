package za.co.armandkamffer.mamba.Commands.Models.CommandMessages;

public class PlanningInvalidCommandMessage {
    public String code;
    public String description;

    public PlanningInvalidCommandMessage(String code, String description) {
        this.code = code;
        this.description = description;
    }
}