package za.co.armandkamffer.mamba.Models.Planning.Representable;

import java.util.ArrayList;

import za.co.armandkamffer.mamba.Models.Planning.PlanningCard;
import za.co.armandkamffer.mamba.Models.Planning.PlanningTicket;
import za.co.armandkamffer.mamba.Models.Planning.PlanningUser;

public class PlanningSessionStateRepresentable {
    private String sessionName;
    private String sessionCode;
    private PlanningCard[] availableCards;
    private ArrayList<PlanningUserRepresentable> participants;
    private PlanningTicketRepresentable ticket;

    public PlanningSessionStateRepresentable(String sessionName, String sessionCode, PlanningCard[] availableCards, ArrayList<PlanningUser> users, PlanningTicket ticket) {
        this.sessionName = sessionName;
        this.sessionCode = sessionCode;
        this.availableCards = availableCards;
        this.participants = new ArrayList<PlanningUserRepresentable>();
        addPartiticpants(users);
        this.ticket = new PlanningTicketRepresentable(ticket);
    }

    private void addPartiticpants(ArrayList<PlanningUser> users) {
        for (PlanningUser user : users) {
            PlanningUserRepresentable partitipant = new PlanningUserRepresentable(user);
            participants.add(partitipant);
        }
    }
}