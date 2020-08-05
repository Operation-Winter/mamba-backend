package za.co.armandkamffer.mamba.Sessions;

import java.util.HashMap;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import za.co.armandkamffer.mamba.Commands.PlanningHostWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.PlanningJoinWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningJoinSessionMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandReceive;
import za.co.armandkamffer.mamba.Controllers.PlanningWebSocketHandler;
import za.co.armandkamffer.mamba.Models.Planning.User;

public final class PlanningSessionManager {
    private static PlanningSessionManager INSTANCE;
    private HashMap<String, PlanningSession> sessions;

    private PlanningHostWebSocketMessageParser hostCommandParser;
    private PlanningJoinWebSocketMessageParser joinCommandParser;

    public synchronized static PlanningSessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlanningSessionManager();
        }
        return INSTANCE;
    }

    private PlanningSessionManager() {
        sessions = new HashMap<String, PlanningSession>();
        hostCommandParser = new PlanningHostWebSocketMessageParser();
        joinCommandParser = new PlanningJoinWebSocketMessageParser();
    }

    private String createSessionID() {
        String sessionID;
        do {
            Integer sessionCount = sessions.size();
            sessionID = String.format("%06d", sessionCount);
        } while (sessions.get(sessionID) != null);

        return sessionID;
    }

    public void parseHostMessageToCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, WebSocketMessage<?> message) {
        PlanningHostCommandReceive command = hostCommandParser.parseMessageToCommand(message);
        executeHostCommand(webSocketHandler, session, command);
    }

    public void parseJoinMessageToCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, WebSocketMessage<?> message) {
        PlanningJoinCommandReceive command = joinCommandParser.parseMessageToCommand(message);
        executeJoinCommand(webSocketHandler, session, command);
    }

    private void executeHostCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, PlanningHostCommandReceive command) {
        switch (command.type) {
            case START_SESSION:
                String newSessionID = createSessionID();
                PlanningSession planningSession = new PlanningSession(newSessionID, webSocketHandler);
                webSocketHandler.addTag(session, "host-" + newSessionID);
                sessions.put(newSessionID, planningSession);
                planningSession.executeCommand(command);
                break;
        
            default:
                break;
        }
    }

    private void executeJoinCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, PlanningJoinCommandReceive command) {
        switch (command.type) {
            case JOIN_SESSION:
                PlanningJoinSessionMessage message = joinCommandParser.parseStartSessionMessage(command.message);
                PlanningSession planningSession = sessions.get(message.sessionCode);

                if (session == null) {
                    // TODO: If invalid send back invalid_session command
                }

                User newUser = new User(message.participantName);
                webSocketHandler.addTag(session, "join-" + planningSession.sessionID);
                planningSession.addUser(newUser);
                break;
        
            default:
                break;
        }
    }
}