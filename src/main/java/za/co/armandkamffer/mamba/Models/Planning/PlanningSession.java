package za.co.armandkamffer.mamba.Models.Planning;

import java.util.ArrayList;
import java.util.UUID;

public class PlanningSession {
    public UUID sessionID;
    public ArrayList<User> users;
    public ArrayList<Card> availableCards;
    public PlanningSessionState state;

    public PlanningSession() {
        sessionID = UUID.randomUUID();
        users = new ArrayList<User>();
        availableCards = new ArrayList<Card>();
        state = PlanningSessionState.NONE;
    }
}