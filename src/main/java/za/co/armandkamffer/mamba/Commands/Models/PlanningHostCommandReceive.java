package za.co.armandkamffer.mamba.Commands.Models;

public enum PlanningHostCommandReceive {
    // Planning Host Receive
    SETUP_SESSION,
    ADD_TICKET,
    SKIP_VOTE,
    REMOVE_USER,
    END_SESSION,
    FINISH_VOTING,
    
    // Planning Host Send/Receive
    SHARE_SESSION
}