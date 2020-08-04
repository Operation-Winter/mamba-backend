package za.co.armandkamffer.mamba.Models.Planning;

public class PlanningSessionStateRepresentable {
    private String sessionName;
    private String sessionCode;
    private Card[] availableCards;

    public PlanningSessionStateRepresentable(String sessionName, String sessionCode, Card[] availableCards) {
        this.sessionName = sessionName;
        this.sessionCode = sessionCode;
        this.availableCards = availableCards;
    }
}