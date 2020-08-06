package za.co.armandkamffer.mamba.Commands;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningHostStartSessionMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandSend;

public class PlanningHostWebSocketMessageParser {
    private Logger logger = LoggerFactory.getLogger(PlanningHostWebSocketMessageParser.class);

    public PlanningHostCommandReceive parseMessageToCommand(WebSocketMessage<?> webSocketMessage) throws Error {
        String webSocketMessageString = parseMessageToString(webSocketMessage);
        return parseStringToCommand(webSocketMessageString);
    }

    public BinaryMessage parseCommandToBinaryMessage(PlanningHostCommandSend command) {
        String jsonMessage = new Gson().toJson(command);
        logger.info("PlanningHost command: {}", jsonMessage);
        return new BinaryMessage(jsonMessage.getBytes());
    }

    public TextMessage parseCommandToTextMessage(PlanningHostCommandSend command) {
        String jsonMessage = new Gson().toJson(command);
        logger.info("PlanningHost command: {}", jsonMessage);
        return new TextMessage(jsonMessage);
    }

    private String parseMessageToString(WebSocketMessage<?> webSocketMessage) throws Error {
        ByteBuffer byteBuffer;

        if (webSocketMessage instanceof TextMessage) {
            byteBuffer = ByteBuffer.wrap(((TextMessage) webSocketMessage).asBytes());
        } else if (webSocketMessage instanceof BinaryMessage) {
            byteBuffer = ((BinaryMessage) webSocketMessage).getPayload();
        } else {
            throw new Error("Invalid WebSocketMessage type");
        }

        return new String(byteBuffer.array(), StandardCharsets.UTF_8);
    }

    private PlanningHostCommandReceive parseStringToCommand(String commandString) {
        return new Gson().fromJson(commandString, PlanningHostCommandReceive.class);
    }

    public PlanningHostStartSessionMessage parseStartSessionMessage(JsonObject messageString) {
        return new Gson().fromJson(messageString, PlanningHostStartSessionMessage.class);
    }
}