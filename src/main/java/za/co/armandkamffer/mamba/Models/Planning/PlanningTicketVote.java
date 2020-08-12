package za.co.armandkamffer.mamba.Models.Planning;

public class PlanningTicketVote {
    public PlanningTicket ticket;
    public PlanningUser user;
    public PlanningCard selectedCard;

    public PlanningTicketVote(PlanningTicket ticket, PlanningUser user, PlanningCard selectedCard) {
        this.ticket = ticket;
        this.user = user;
        this.selectedCard = selectedCard;
    }
}