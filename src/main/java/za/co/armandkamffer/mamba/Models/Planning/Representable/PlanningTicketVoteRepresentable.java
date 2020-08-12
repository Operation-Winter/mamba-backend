package za.co.armandkamffer.mamba.Models.Planning.Representable;

import za.co.armandkamffer.mamba.Models.Planning.PlanningCard;

public class PlanningTicketVoteRepresentable {
    public PlanningUserRepresentable user;
    public PlanningCard selectedCard;

    public PlanningTicketVoteRepresentable(PlanningUserRepresentable user, PlanningCard selectedCard) {
        this.user = user;
        this.selectedCard = selectedCard;
    }
}