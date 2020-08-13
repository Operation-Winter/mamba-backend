package za.co.armandkamffer.mamba.Sessions;

import java.util.HashMap;
import java.util.Optional;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import za.co.armandkamffer.mamba.Commands.PlanningHostWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.PlanningJoinWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningJoinCommandSendType;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningInvalidCommandMessage;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningJoinSessionMessage;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningVoteMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandSend;
import za.co.armandkamffer.mamba.Controllers.PlanningWebSocketHandler;
import za.co.armandkamffer.mamba.Models.Planning.PlanningUser;

public final class PlanningSessionManager {
    private Logger logger = LoggerFactory.getLogger(PlanningSessionManager.class);

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

    public void parseHostMessageToCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, String sessionCode, WebSocketMessage<?> message) {
        PlanningHostCommandReceive command = hostCommandParser.parseMessageToCommand(message);
        executeHostCommand(webSocketHandler, session, sessionCode, command);
    }

    public void parseJoinMessageToCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, String sessionCode, WebSocketMessage<?> message) {
        PlanningJoinCommandReceive command = joinCommandParser.parseMessageToCommand(message);
        executeJoinCommand(webSocketHandler, session, sessionCode, command);
    }

    private void executeHostCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, String sessionCode, PlanningHostCommandReceive command) {
        switch (command.type) {
            case START_SESSION:
                executeHostSessionCommand(webSocketHandler, session, command);
                break;
        
            default:
                if (sessionCode == null) {
                    sendInvalidCommand(webSocketHandler, session, "0000", "No session code has been specified");
                    return;
                }
                
                PlanningSession planningSession = sessions.get(sessionCode);
                if (planningSession == null) {
                    sendInvalidCommand(webSocketHandler, session, "1000", "Session is not available anymore");
                    return;
                }
                planningSession.executeCommand(command);
                break;
        }
    }

    private void executeJoinCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, String sessionCode, PlanningJoinCommandReceive command) {
        switch (command.type) {
            case JOIN_SESSION:
                executeJoinSessionCommand(webSocketHandler, session, command);
                break;
        
            case VOTE:
                executeVoteCommand(webSocketHandler, session, sessionCode, command);
                break;

            default:
                break;
        }
    }

    private void sendInvalidCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, String code, String description) {
        logger.warn("Invalid command for {}: {}:{}", session.getId(), code, description);
        PlanningInvalidCommandMessage invalidCommandMessage = new PlanningInvalidCommandMessage(code, description);
        JsonObject message = joinCommandParser.parseMessageToJson(invalidCommandMessage);
        PlanningJoinCommandSend invalidCommand = new PlanningJoinCommandSend(PlanningJoinCommandSendType.INVALID_COMMAND, message);
        webSocketHandler.sendMessage(session.getId(), joinCommandParser.parseCommandToBinaryMessage(invalidCommand));
    }

    private void executeHostSessionCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, PlanningHostCommandReceive command) {
        String newSessionID = createSessionID();
        PlanningSession planningSession = new PlanningSession(newSessionID, webSocketHandler);
        webSocketHandler.setSessionCodeTag(session, "host", newSessionID);
        sessions.put(newSessionID, planningSession);
        planningSession.executeCommand(command);
        logger.info("Planning Session created: {}", newSessionID);
    }

    private void executeJoinSessionCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, PlanningJoinCommandReceive command) {
        PlanningJoinSessionMessage message = joinCommandParser.parseJoinSessionMessage(command.message);
        PlanningSession planningSession = sessions.get(message.sessionCode);

        if (planningSession == null) {
            logger.warn("Invalid session code for {}: {}", session.getId(), message.sessionCode);
            PlanningJoinCommandSend invalidSessionCommand = new PlanningJoinCommandSend(PlanningJoinCommandSendType.INVALID_SESSION, null);
            webSocketHandler.sendMessage(session.getId(), joinCommandParser.parseCommandToBinaryMessage(invalidSessionCommand));
            return;
        }

        PlanningUser newUser = new PlanningUser(message.participantName, session.getId());
        webSocketHandler.setSessionCodeTag(session, "join", planningSession.sessionID);
        session.getAttributes().put("participantId", newUser.identifier.toString());
        planningSession.addUser(newUser);
        logger.info("Participant added to Planning Session: {}", planningSession.sessionID);
    }

    private void executeVoteCommand(PlanningWebSocketHandler webSocketHandler, WebSocketSession session, String sessionCode, PlanningJoinCommandReceive command) {
        if (sessionCode == null) {
            logger.warn("No session code specified for {}", session.getId());
            sendInvalidCommand(webSocketHandler, session, "0000", "No session code has been specified");
            return;
        }
        PlanningSession planningSession = sessions.get(sessionCode);
        if (planningSession == null) {
            logger.warn("Session is not available for {}: {}", session.getId(), sessionCode);
            sendInvalidCommand(webSocketHandler, session, "1000", "Session is not available anymore");
            return;
        }
        String participantId = (String) session.getAttributes().get("participantId");
        if (participantId == null) {
            logger.warn("Participant ID is not available for {}: {}", session.getId(), sessionCode);
            sendInvalidCommand(webSocketHandler, session, "", "No participant ID is available");
            return;
        }

        PlanningVoteMessage message = joinCommandParser.parseVoteMessage(command.message);

        Optional<PlanningUser> user = planningSession.getUser(participantId);
        if(!user.isPresent()) {
            logger.warn("Session code for {}: {}", session.getId(), sessionCode);
            sendInvalidCommand(webSocketHandler, session, "", "Participant is not part of session");
            return;
        }

        planningSession.addVote(message.ticketId, user.get(), message.selectedCard);
    }
}