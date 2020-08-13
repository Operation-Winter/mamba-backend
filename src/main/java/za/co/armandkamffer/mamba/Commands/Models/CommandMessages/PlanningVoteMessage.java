package za.co.armandkamffer.mamba.Commands.Models.CommandMessages;

import za.co.armandkamffer.mamba.Models.Planning.PlanningCard;

public class PlanningVoteMessage {
    public String ticketId;
    public PlanningCard selectedCard;
}