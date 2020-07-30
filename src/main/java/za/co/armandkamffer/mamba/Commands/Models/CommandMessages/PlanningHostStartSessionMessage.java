package za.co.armandkamffer.mamba.Commands.Models.CommandMessages;

import za.co.armandkamffer.mamba.Models.Planning.Card;

public class PlanningHostStartSessionMessage {
    public String sessionName;
    public Card[] availableCards;
}