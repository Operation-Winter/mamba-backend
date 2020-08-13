package za.co.armandkamffer.mamba.Models.Planning.Representable;

import za.co.armandkamffer.mamba.Models.Planning.PlanningUser;

public class PlanningUserRepresentable {
    public String id;
    public String name;

    public PlanningUserRepresentable(PlanningUser user) {
        this.id = user.identifier.toString();
        this.name = user.name;
    }
}