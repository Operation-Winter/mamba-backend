package za.co.armandkamffer.mamba.Sessions;

import java.util.HashMap;

import org.springframework.web.socket.WebSocketMessage;

import za.co.armandkamffer.mamba.Commands.PlanningHostWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.PlanningJoinWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningJoinSessionMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandReceive;
import za.co.armandkamffer.mamba.Controllers.PlanningHostWebSocketHandler;
import za.co.armandkamffer.mamba.Controllers.PlanningJoinWebSocketHandler;
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

    public void parseHostMessageToCommand(PlanningHostWebSocketHandler webSocketHandler, String sessionID, WebSocketMessage<?> message) {
        PlanningHostCommandReceive command = hostCommandParser.parseMessageToCommand(message);
        executeHostCommand(webSocketHandler, command, sessionID);
    }

    public void parseJoinMessageToCommand(PlanningJoinWebSocketHandler webSocketHandler, String sessionID, WebSocketMessage<?> message) {
        PlanningJoinCommandReceive command = joinCommandParser.parseMessageToCommand(message);
        executeJoinCommand(webSocketHandler, command, sessionID);
    }

    private void executeHostCommand(PlanningHostWebSocketHandler webSocketHandler, PlanningHostCommandReceive command, String sessionID) {
        switch (command.type) {
            case START_SESSION:
                String newSessionID = createSessionID();
                PlanningSession session = new PlanningSession(newSessionID, webSocketHandler);
                webSocketHandler.sessionID = newSessionID;
                sessions.put(newSessionID, session);
                session.executeCommand(command);
                break;
        
            default:
                break;
        }
    }

    private void executeJoinCommand(PlanningJoinWebSocketHandler webSocketHandler, PlanningJoinCommandReceive command, String sessionID) {
        switch (command.type) {
            case JOIN_SESSION:
                PlanningJoinSessionMessage message = joinCommandParser.parseStartSessionMessage(command.message);
                PlanningSession session = sessions.get(message.sessionCode);

                if (session == null) {
                    // TODO: If invalid send back invalid_session command
                }

                User newUser = new User(message.participantName, webSocketHandler);
                webSocketHandler.sessionID = session.sessionID;
                session.addUser(newUser);
                break;
        
            default:
                break;
        }
    }
}