package za.co.armandkamffer.mamba.Controllers;

import java.util.UUID;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;

import za.co.armandkamffer.mamba.Commands.Models.Command;
import za.co.armandkamffer.mamba.Sessions.PlanningSessionManager;

@WebServlet
public class PlanningHostWebSocketHandler implements WebSocketHandler {
    private UUID sessionID;
    Logger logger = LoggerFactory.getLogger(PlanningHostWebSocketHandler.class);

    @Override
    public void handleMessage(org.springframework.web.socket.WebSocketSession session, WebSocketMessage<?> message)
            throws Exception {
        Command command = PlanningSessionManager.getInstance().parseHostMessageToCommand(sessionID, message);

        Command testCommand = new Command("NEW_PARTICIPANT", null);
        BinaryMessage binaryMessage = PlanningSessionManager.getInstance().parseHostCommandToMessage(testCommand);

        session.sendMessage(binaryMessage);
    }

    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
        sessionID = PlanningSessionManager.getInstance().createPlanningSession();
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

}