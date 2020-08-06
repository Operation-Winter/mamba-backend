package za.co.armandkamffer.mamba.Models.Planning;

import java.util.ArrayList;

public class PlanningSessionStateRepresentable {
    private String sessionName;
    private String sessionCode;
    private PlanningCard[] availableCards;
    private ArrayList<ParticipantRepresentable> participants;
    private PlanningTicket ticket;

    public PlanningSessionStateRepresentable(String sessionName, String sessionCode, PlanningCard[] availableCards, ArrayList<PlanningUser> users, PlanningTicket ticket) {
        this.sessionName = sessionName;
        this.sessionCode = sessionCode;
        this.availableCards = availableCards;
        this.participants = new ArrayList<ParticipantRepresentable>();
        addPartiticpants(users);
        this.ticket = ticket;
    }

    private void addPartiticpants(ArrayList<PlanningUser> users) {
        for (PlanningUser user : users) {
            ParticipantRepresentable partitipant = new ParticipantRepresentable(user.identifier.toString(), user.name);
            participants.add(partitipant);
        }
    }
}