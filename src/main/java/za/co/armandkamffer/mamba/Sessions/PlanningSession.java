package za.co.armandkamffer.mamba.Sessions;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;

import za.co.armandkamffer.mamba.Commands.PlanningHostWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.PlanningJoinWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningHostCommandSendType;
import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningJoinCommandSendType;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningAddTicketMessage;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningHostStartSessionMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandSend;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandSend;
import za.co.armandkamffer.mamba.Controllers.PlanningWebSocketHandler;
import za.co.armandkamffer.mamba.Models.Planning.PlanningCard;
import za.co.armandkamffer.mamba.Models.Planning.PlanningSessionState;
import za.co.armandkamffer.mamba.Models.Planning.PlanningSessionStateRepresentable;
import za.co.armandkamffer.mamba.Models.Planning.PlanningTicket;
import za.co.armandkamffer.mamba.Models.Planning.PlanningUser;

public class PlanningSession {
    private Logger logger = LoggerFactory.getLogger(PlanningSession.class);
    private PlanningHostWebSocketMessageParser hostCommandParser;
    private PlanningJoinWebSocketMessageParser joinCommandParser;
    private String sessionName;
    private ArrayList<PlanningUser> users;
    private PlanningCard[] availableCards;
    private PlanningSessionState state;
    private PlanningWebSocketHandler webSocketHandler;
    private PlanningTicket ticket;
    public String sessionID;

    public PlanningSession(String sessionID, PlanningWebSocketHandler webSocketHandler) {
        this.sessionID = sessionID;
        this.webSocketHandler = webSocketHandler;
        users = new ArrayList<PlanningUser>();
        state = PlanningSessionState.NONE;
        hostCommandParser = new PlanningHostWebSocketMessageParser();
        joinCommandParser = new PlanningJoinWebSocketMessageParser();
        logger.info("Planning session created: {} {}", sessionName, sessionID);
    }

    public void addUser(PlanningUser user) {
        users.add(user);
        sendCurrentStateToAll();
    }

    private PlanningSessionStateRepresentable createSessionStateRepresentable() {
        return new PlanningSessionStateRepresentable(sessionName, 
                                                     sessionID,
                                                     availableCards,
                                                     users,
                                                     ticket);
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
                PlanningHostStartSessionMessage hostStartMessage = hostCommandParser.parseStartSessionMessage(command.message);
                sessionName = hostStartMessage.sessionName;
                availableCards = hostStartMessage.availableCards;
                sendCurrentStateToAll();
                break;

            case ADD_TICKET:
                PlanningAddTicketMessage addTicketMessage = hostCommandParser.parseAddTicketMessage(command.message);
                ticket = new PlanningTicket(addTicketMessage.identifier, addTicketMessage.description);
                state = PlanningSessionState.VOTING;
                sendCurrentStateToAll();
                break;

            default:
                break;
        }
    }
    
    private void sendCommandToHost(PlanningHostCommandSend command) {
        BinaryMessage message = hostCommandParser.parseCommandToBinaryMessage(command);
        webSocketHandler.sendMessage(sessionID, "host", message);
    }

    private void sendCommandToPartitipants(PlanningJoinCommandSend command) {
        BinaryMessage message = joinCommandParser.parseCommandToBinaryMessage(command);
        webSocketHandler.sendMessage(sessionID, "join", message);
    }
}