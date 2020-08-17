package za.co.armandkamffer.mamba.Commands.Models.CommandMessages;

public class PlanningRemoveParticipantMessage {
    public String participantId;

    public PlanningRemoveParticipantMessage(String participantId) {
        this.participantId = participantId;
    }
}