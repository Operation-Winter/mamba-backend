package za.co.armandkamffer.mamba.Sessions;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketMessage;

import za.co.armandkamffer.mamba.Commands.PlanningHostWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandSend;
import za.co.armandkamffer.mamba.Controllers.PlanningHostWebSocketHandler;

public final class PlanningSessionManager {
    private static PlanningSessionManager INSTANCE;
    private HashMap<UUID, PlanningSession> sessions;
    private HashMap<UUID, PlanningHostWebSocketHandler> hosts;

    private PlanningHostWebSocketMessageParser parser;

    public synchronized static PlanningSessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlanningSessionManager();
        }
        return INSTANCE;
    }

    private PlanningSessionManager() {
        sessions = new HashMap<UUID, PlanningSession>();
        hosts = new HashMap<UUID, PlanningHostWebSocketHandler>();
        parser = new PlanningHostWebSocketMessageParser();
    }

    public UUID createPlanningSession() {
        PlanningSession session = new PlanningSession();
        sessions.put(session.sessionID, session);
        return session.sessionID;
    }

    public void parseHostMessageToCommand(PlanningHostWebSocketHandler webSocketHandler, UUID sessionID, WebSocketMessage<?> message) {
        PlanningHostCommandReceive command = parser.parseMessageToCommand(message);
        executeHostCommand(webSocketHandler, command, sessionID);
    }

    public BinaryMessage parseHostCommandToMessage(PlanningHostCommandSend command) {
        BinaryMessage message = parser.parseCommandToMessage(command);
        return message;
    }

    private void executeHostCommand(PlanningHostWebSocketHandler webSocketHandler, PlanningHostCommandReceive command, UUID sessionID) {
        switch (command.type) {
            case START_SESSION:
                UUID newSessionID = createPlanningSession();
                webSocketHandler.sessionID = newSessionID;
                hosts.put(newSessionID, webSocketHandler);
                sessions.get(newSessionID).executeCommand(command);
                break;
        
            default:
                break;
        }
    }

    public void sendCommandToHost(PlanningHostCommandSend command, UUID sessionID) {
        BinaryMessage binaryMessage = parseHostCommandToMessage(command);
        PlanningHostWebSocketHandler hostWebSocketHandler = hosts.get(sessionID);

        try {
            hostWebSocketHandler.sendCommand(binaryMessage);
        } catch (Exception e) {
            //TODO: handle exception
        }
        
    }
}