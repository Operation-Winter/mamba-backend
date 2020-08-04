package za.co.armandkamffer.mamba.Sessions;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.web.socket.BinaryMessage;

import za.co.armandkamffer.mamba.Commands.PlanningHostWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.PlanningJoinWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningHostCommandSendType;
import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningJoinCommandSendType;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningHostStartSessionMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandSend;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandSend;
import za.co.armandkamffer.mamba.Controllers.PlanningHostWebSocketHandler;
import za.co.armandkamffer.mamba.Models.Planning.Card;
import za.co.armandkamffer.mamba.Models.Planning.PlanningSessionState;
import za.co.armandkamffer.mamba.Models.Planning.PlanningSessionStateRepresentable;
import za.co.armandkamffer.mamba.Models.Planning.User;

public class PlanningSession {
    private PlanningHostWebSocketMessageParser hostCommandParser;
    private PlanningJoinWebSocketMessageParser joinCommandParser;
    private String sessionName;
    private ArrayList<User> users;
    private Card[] availableCards;
    private PlanningSessionState state;
    private PlanningHostWebSocketHandler hostWebSocketHandler;
    public String sessionID;

    public PlanningSession(String sessionID, PlanningHostWebSocketHandler hostWebSocketHandler) {
        this.sessionID = sessionID;
        this.hostWebSocketHandler = hostWebSocketHandler;
        users = new ArrayList<User>();
        state = PlanningSessionState.NONE;
        hostCommandParser = new PlanningHostWebSocketMessageParser();
        joinCommandParser = new PlanningJoinWebSocketMessageParser();
    }

    public void addUser(User user) {
        users.add(user);
        sendCurrentStateToAll();
    }

    private PlanningSessionStateRepresentable createSessionStateRepresentable() {
        return new PlanningSessionStateRepresentable(sessionName, 
                                                     sessionID,
                                                     availableCards,
                                                     users);
    }

    private void sendCurrentStateToAll() {
        PlanningSessionStateRepresentable sessionState = createSessionStateRepresentable();
        String messageString = new Gson().toJson(sessionState);
        JsonObject message = JsonParser.parseString(messageString).getAsJsonObject();
        PlanningHostCommandSendType hostCommandType = PlanningHostCommandSendType.NONE_STATE;
        PlanningJoinCommandSendType joinCommandType = PlanningJoinCommandSendType.NONE_STATE;

        switch (this.state) {
            case NONE:
                hostCommandType = PlanningHostCommandSendType.NONE_STATE;
                joinCommandType = PlanningJoinCommandSendType.NONE_STATE;
                break;
            case VOTING:
                hostCommandType = PlanningHostCommandSendType.VOTING_STATE;
                joinCommandType = PlanningJoinCommandSendType.VOTING_STATE;
                break;
            case VOTING_FINISHED:
                hostCommandType = PlanningHostCommandSendType.FINISHED_STATE;
                joinCommandType = PlanningJoinCommandSendType.FINISHED_STATE;
                break;
        }

        sendCommandToHost(new PlanningHostCommandSend(hostCommandType, message));
        sendCommandToPartitipants(new PlanningJoinCommandSend(joinCommandType, message));
    }

    public void executeCommand(PlanningHostCommandReceive command) {
        switch (command.type) {
            case START_SESSION:
                PlanningHostStartSessionMessage message = new PlanningHostWebSocketMessageParser().parseStartSessionMessage(command.message);
                sessionName = message.sessionName;
                availableCards = message.availableCards;
                sendCurrentStateToAll();
                break;

            default:
                break;
        }
    }
    
    private void sendCommandToHost(PlanningHostCommandSend command) {
        BinaryMessage binaryMessage = hostCommandParser.parseCommandToMessage(command);

        try {
            hostWebSocketHandler.sendCommand(binaryMessage);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private void sendCommandToPartitipants(PlanningJoinCommandSend command) {
        BinaryMessage binaryMessage = joinCommandParser.parseCommandToMessage(command);

        for (User user : users) {
            try {
                user.webSocketHandler.sendCommand(binaryMessage);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }
}