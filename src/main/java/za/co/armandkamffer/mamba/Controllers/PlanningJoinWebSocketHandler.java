package za.co.armandkamffer.mamba.Controllers;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;

import za.co.armandkamffer.mamba.Sessions.PlanningSessionManager;

@WebServlet
public class PlanningJoinWebSocketHandler implements WebSocketHandler {
    private org.springframework.web.socket.WebSocketSession session;
    Logger logger = LoggerFactory.getLogger(PlanningJoinWebSocketHandler.class);
    private PlanningSessionManager planningSessionManager;
    public String sessionID;

    @Override
    public void handleMessage(org.springframework.web.socket.WebSocketSession session, WebSocketMessage<?> message)
            throws Exception {
        planningSessionManager.parseJoinMessageToCommand(this, sessionID, message);
    }

    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
        this.session = session;
        planningSessionManager = PlanningSessionManager.getInstance();
    }

    @Override
    public void handleTransportError(org.springframework.web.socket.WebSocketSession session, Throwable exception)
            throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void afterConnectionClosed(org.springframework.web.socket.WebSocketSession session, CloseStatus closeStatus)
            throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendCommand(BinaryMessage binaryMessage) throws IOException {
        session.sendMessage(binaryMessage);
    }
}