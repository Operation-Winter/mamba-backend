package za.co.armandkamffer.mamba.Models;

public class PlanningSession {
    private String name;

    public PlanningSession() { }

    public PlanningSession(String name) {
        this.name = name;
    }

    public String getContent() {
        return name;
    }
}