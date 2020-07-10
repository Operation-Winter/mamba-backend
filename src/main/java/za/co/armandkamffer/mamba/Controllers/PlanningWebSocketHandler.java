package za.co.armandkamffer.mamba.Controllers;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;

@WebServlet
public class PlanningWebSocketHandler implements WebSocketHandler {

    Logger logger = LoggerFactory.getLogger(PlanningWebSocketHandler.class);

    @Override
    public void handleMessage(org.springframework.web.socket.WebSocketSession session, WebSocketMessage<?> message)
            throws Exception {
        // TODO Auto-generated method stub
        logger.info(message.getPayload().toString());
    }

    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub
        return false;
    }

}