package za.co.armandkamffer.mamba.Controllers;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;

import za.co.armandkamffer.mamba.Models.Planning.PlanningSessionTags;
import za.co.armandkamffer.mamba.Sessions.PlanningSessionManager;

public class PlanningWebSocketHandler implements WebSocketHandler {
    Logger logger = LoggerFactory.getLogger(PlanningWebSocketHandler.class);
    private PlanningSessionManager planningSessionManager = PlanningSessionManager.getInstance();
    private final PlanningSessionTags<WebSocketSession, String> tags = new PlanningSessionTags<>();

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String type = (String) session.getAttributes().get("type");
        String sessionCode = (String) session.getAttributes().get("sessionCode");
        logger.info("Command received from {}:{}:{}", session.getId(), type, sessionCode);

        if(type.equals("host")) {
            planningSessionManager.parseHostMessageToCommand(this, session, sessionCode, message);
        } else if(type.equals("join")) {
            planningSessionManager.parseJoinMessageToCommand(this, session, sessionCode, message);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Session closed ID: {}", session.getId());
        tags.add(session, session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Session error. Removing session ID: " + session.getId(), exception);
        try {
            session.close();
        } catch (Exception ex) {
            logger.warn("Error closing session ID: " + session.getId(), ex);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("Session closed ID: {}", session.getId());
        tags.removeObject(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendMessage(String toTag, WebSocketMessage<?> message) {
        tags.getObjectsWith(toTag).parallelStream().forEach(sessionTo -> {
            try {
                if (sessionTo.isOpen()) {
                    sessionTo.sendMessage(message);
                    logger.info("Message sent from session {} to session with tag {}", sessionTo.getId(), toTag);
                } else {
                    logger.warn("Message not sent from session {} to session with tag {}", sessionTo.getId(), toTag);
                }
            } catch (Exception ex) {
                logger.warn("Error sending message to session ID: " + sessionTo.getId(), ex);
            }
        });
    }

    public void sendMessage(String toTag, String toType, WebSocketMessage<?> message) {
        String tag = toType + "-" + toTag;
        sendMessage(tag, message);
    }

    public void setSessionCodeTag(WebSocketSession session, String type, String sessionCode) {
        session.getAttributes().put("sessionCode", sessionCode);
        addTag(session, type + "-" + sessionCode);
    }

    private void addTag(WebSocketSession session, String tag) {
        tags.add(session, tag);
        logger.info("Add tag: Session ID: {} received tag {}", session.getId(), tag);
    }
    
    private void removeTag(WebSocketSession session, String tag) {
        tags.removeTagFrom(session, tag);
        logger.info("Remove tag: Session ID: {} removed tag {}", session.getId(), tag);
    }
}