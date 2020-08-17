package za.co.armandkamffer.mamba.Commands.Models.CommandMessages;

public class PlanningSkipVoteMessage {
    public String participantId;

    public PlanningSkipVoteMessage(String participantId) {
        this.participantId = participantId;
    }
}