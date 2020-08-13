package za.co.armandkamffer.mamba.Models.Planning;

import java.util.ArrayList;

public class PlanningTicket {
    public String identifier;
    public String description;
    public ArrayList<PlanningTicketVote> ticketVotes;

    public PlanningTicket(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
        this.ticketVotes = new ArrayList<PlanningTicketVote>();
    }

    public void resetTicketVotes() {
        ticketVotes.clear();
    }
}