package za.co.armandkamffer.mamba.Sessions;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketMessage;

import za.co.armandkamffer.mamba.Commands.WebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.Models.Command;
import za.co.armandkamffer.mamba.Models.Planning.PlanningSession;

public final class PlanningSessionManager {
    private static PlanningSessionManager INSTANCE;
    private HashMap<UUID, PlanningSession> sessions;

    public synchronized static PlanningSessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlanningSessionManager();
        }
        return INSTANCE;
    }

    private PlanningSessionManager() {
        sessions = new HashMap<UUID, PlanningSession>();
    }

    public UUID createPlanningSession() {
        PlanningSession session = new PlanningSession();
        sessions.put(session.sessionID, session);
        return session.sessionID;
    }

    public Command parseHostMessageToCommand(UUID sessiodID, WebSocketMessage<?> message) {
        Command command = WebSocketMessageParser.parseMessageToCommand(message);
        return command;
    }

    public BinaryMessage parseHostCommandToMessage(Command command) {
        BinaryMessage message = WebSocketMessageParser.parseCommandToMessage(command);
        return message;
    }
}