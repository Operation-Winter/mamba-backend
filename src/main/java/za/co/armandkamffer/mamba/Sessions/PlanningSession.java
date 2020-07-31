package za.co.armandkamffer.mamba.Sessions;

import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import za.co.armandkamffer.mamba.Commands.PlanningHostWebSocketMessageParser;
import za.co.armandkamffer.mamba.Commands.Models.CommandKeys.PlanningHostCommandSendType;
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningHostStartSessionMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandSend;
import za.co.armandkamffer.mamba.Models.Planning.Card;
import za.co.armandkamffer.mamba.Models.Planning.PlanningSessionState;
import za.co.armandkamffer.mamba.Models.Planning.User;

public class PlanningSession {
    public UUID sessionID;
    private String sessionCode;
    private String sessionName;
    private ArrayList<User> users;
    private Card[] availableCards;
    private PlanningSessionState state;

    public PlanningSession() {
        sessionID = UUID.randomUUID();
        users = new ArrayList<User>();
        state = PlanningSessionState.NONE;
    }

    private void sendCurrentStateToAll() {
        String messageString = new Gson().toJson(this);
        JsonObject message = JsonParser.parseString(messageString).getAsJsonObject();
        PlanningHostCommandSendType commandType = PlanningHostCommandSendType.NONE_STATE;
        
        switch (this.state) {
            case NONE:
                commandType = PlanningHostCommandSendType.NONE_STATE;
                break;
            case VOTING:
                commandType = PlanningHostCommandSendType.VOTING_STATE;
                break;
            case VOTING_FINISHED:
                commandType = PlanningHostCommandSendType.FINISHED_STATE;
                break;
        }

        sendCommandToHost(new PlanningHostCommandSend(commandType, message));
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
        PlanningSessionManager.getInstance().sendCommandToHost(command, sessionID);
    }
}