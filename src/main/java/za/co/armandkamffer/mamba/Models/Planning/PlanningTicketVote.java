package za.co.armandkamffer.mamba.Models.Planning;

public class PlanningTicketVote {
    public PlanningUser user;
    public PlanningCard selectedCard;

    public PlanningTicketVote(PlanningUser user, PlanningCard selectedCard) {
        this.user = user;
        this.selectedCard = selectedCard;
    }
}