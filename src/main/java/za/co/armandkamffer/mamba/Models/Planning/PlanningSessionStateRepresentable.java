package za.co.armandkamffer.mamba.Models.Planning;

import java.util.ArrayList;

public class PlanningSessionStateRepresentable {
    private String sessionName;
    private String sessionCode;
    private Card[] availableCards;
    private ArrayList<ParticipantRepresentable> participants;

    public PlanningSessionStateRepresentable(String sessionName, String sessionCode, Card[] availableCards, ArrayList<User> users) {
        this.sessionName = sessionName;
        this.sessionCode = sessionCode;
        this.availableCards = availableCards;
        this.participants = new ArrayList<ParticipantRepresentable>();
        addPartiticpants(users);
    }

    private void addPartiticpants(ArrayList<User> users) {
        for (User user : users) {
            ParticipantRepresentable partitipant = new ParticipantRepresentable(user.identifier.toString(), user.name);
            participants.add(partitipant);
        }
    }
}