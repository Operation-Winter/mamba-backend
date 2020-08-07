package za.co.armandkamffer.mamba.Commands.Models.CommandMessages;

import za.co.armandkamffer.mamba.Models.Planning.PlanningCard;

public class PlanningHostStartSessionMessage {
    public String sessionName;
    public PlanningCard[] availableCards;
}