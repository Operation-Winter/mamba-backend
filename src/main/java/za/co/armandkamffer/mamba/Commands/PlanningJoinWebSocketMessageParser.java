package za.co.armandkamffer.mamba.Commands;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningJoinSessionMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandSend;

public class PlanningJoinWebSocketMessageParser {
    private Logger logger = LoggerFactory.getLogger(PlanningJoinWebSocketMessageParser.class);

    public PlanningJoinCommandReceive parseMessageToCommand(WebSocketMessage<?> webSocketMessage) throws Error {
        String webSocketMessageString = parseMessageToString(webSocketMessage);
        logger.debug("PlanningJoin command: {}", webSocketMessageString);
        return parseStringToCommand(webSocketMessageString);
    }

    public BinaryMessage parseCommandToBinaryMessage(PlanningJoinCommandSend command) {
        String jsonMessage = new Gson().toJson(command);
        logger.debug("PlanningJoin command to BinaryMessage: {}", jsonMessage);
        return new BinaryMessage(jsonMessage.getBytes());
    }

    public TextMessage parseCommandToTextMessage(PlanningJoinCommandSend command) {
        String jsonMessage = new Gson().toJson(command);
        logger.debug("PlanningJoin command to TextMessage: {}", jsonMessage);
        return new TextMessage(jsonMessage);
    }

    public JsonObject parseMessageToJson(Object message) {
        String messageString = new Gson().toJson(message);
        logger.debug("PlanningJoin command message to JSON: {}", messageString);
        return JsonParser.parseString(messageString).getAsJsonObject();
    }

    private String parseMessageToString(WebSocketMessage<?> webSocketMessage) throws Error {
        ByteBuffer byteBuffer;

        if (webSocketMessage instanceof TextMessage) {
            byteBuffer = ByteBuffer.wrap(((TextMessage) webSocketMessage).asBytes());
        } else if (webSocketMessage instanceof BinaryMessage) {
            byteBuffer = ((BinaryMessage) webSocketMessage).getPayload();
        } else {
            logger.error("parseMessageToString: Invalid WebSocketMessage type");
            throw new Error("Invalid WebSocketMessage type");
        }

        return new String(byteBuffer.array(), StandardCharsets.UTF_8);
    }

    private PlanningJoinCommandReceive parseStringToCommand(String commandString) {
        return new Gson().fromJson(commandString, PlanningJoinCommandReceive.class);
    }

    public PlanningJoinSessionMessage parseJoinSessionMessage(JsonObject messageString) {
        return new Gson().fromJson(messageString, PlanningJoinSessionMessage.class);
    }
}