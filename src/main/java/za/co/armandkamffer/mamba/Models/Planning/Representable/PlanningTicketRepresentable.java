package za.co.armandkamffer.mamba.Models.Planning.Representable;

import java.util.ArrayList;

import za.co.armandkamffer.mamba.Models.Planning.PlanningTicket;
import za.co.armandkamffer.mamba.Models.Planning.PlanningTicketVote;

public class PlanningTicketRepresentable {
    public String identifier;
    public String description;
    public ArrayList<PlanningTicketVoteRepresentable> ticketVotes = new ArrayList<PlanningTicketVoteRepresentable>();

    public PlanningTicketRepresentable(PlanningTicket ticket) {
        this.identifier = ticket.identifier;
        this.description = ticket.description;

        for (PlanningTicketVote ticketVote : ticket.ticketVotes) {
            PlanningUserRepresentable user = new PlanningUserRepresentable(ticketVote.user);
            ticketVotes.add(new PlanningTicketVoteRepresentable(user, ticketVote.selectedCard));
        }
    }
}