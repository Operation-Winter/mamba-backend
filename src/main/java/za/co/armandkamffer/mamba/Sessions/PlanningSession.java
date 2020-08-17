package za.co.armandkamffer.mamba.Sessions;

import java.util.ArrayList;
import java.util.Optional;

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
import za.co.armandkamffer.mamba.Commands.Models.CommandMessages.PlanningRemoveParticipantMessage;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandReceive;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningHostCommandSend;
import za.co.armandkamffer.mamba.Commands.Models.Commands.PlanningJoinCommandSend;
import za.co.armandkamffer.mamba.Controllers.PlanningWebSocketHandler;
import za.co.armandkamffer.mamba.Models.Planning.PlanningCard;
import za.co.armandkamffer.mamba.Models.Planning.PlanningSessionState;
import za.co.armandkamffer.mamba.Models.Planning.PlanningTicket;
import za.co.armandkamffer.mamba.Models.Planning.PlanningTicketVote;
import za.co.armandkamffer.mamba.Models.Planning.PlanningUser;
import za.co.armandkamffer.mamba.Models.Planning.Representable.PlanningSessionStateRepresentable;

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
        logger.info("Participant {} added to {}", user.name, sessionID);
    }

    public Optional<PlanningUser> getUser(String participantId) {
        Optional<PlanningUser> user = users.stream().filter(x -> x.identifier.toString().equals(participantId)).findFirst();
        return user;
    }

    public void removeUser(PlanningUser user) {
        users.remove(user);
        sendCurrentStateToAll();;
    }

    private PlanningSessionStateRepresentable createSessionStateRepresentable() {
        return new PlanningSessionStateRepresentable(sessionName, sessionID, availableCards, users, ticket);
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
        logger.info("Current state {} sent to all on {}", state, sessionID);
    }

    public void executeCommand(PlanningHostCommandReceive command) {
        logger.info("{} host command received on {}", command.type, sessionID);
        switch (command.type) {
            case START_SESSION:
                executeStartSessionCommand(command);
                break;

            case ADD_TICKET:
                executeAddTicketCommand(command);
                break;

            case REVOTE:
                executeRevoteCommand();
                break;
                
            case FINISH_VOTING:
                executeFinishVotingCommand();
                break;

            case REMOVE_PARTICIPANT:
                executeRemoveParticipantCommand(command);
                break;

            default:
                logger.warn("Command received with no implementation: {}", command.type);
                break;
        }
    }

    public void addVote(String ticketId, PlanningUser user, PlanningCard card) {
        if(!ticket.identifier.equals(ticketId) && state != PlanningSessionState.VOTING) {
            return;
        }

        Optional<PlanningTicketVote> existingVote = ticket.ticketVotes.stream().filter(x -> x.user == user).findFirst();
        if(existingVote.isPresent()) {
            ticket.ticketVotes.remove(existingVote.get());
        }

        PlanningTicketVote ticketVote = new PlanningTicketVote(user, card);
        ticket.ticketVotes.add(ticketVote);

        if(ticket.ticketVotes.size() == users.size()) {
            state = PlanningSessionState.VOTING_FINISHED;
        }

        sendCurrentStateToAll();
    }

    private void executeStartSessionCommand(PlanningHostCommandReceive command) {
        PlanningHostStartSessionMessage hostStartMessage = hostCommandParser.parseStartSessionMessage(command.message);
        sessionName = hostStartMessage.sessionName;
        availableCards = hostStartMessage.availableCards;
        sendCurrentStateToAll();
    }

    private void executeAddTicketCommand(PlanningHostCommandReceive command) {
        PlanningAddTicketMessage addTicketMessage = hostCommandParser.parseAddTicketMessage(command.message);
        ticket = new PlanningTicket(addTicketMessage.identifier, addTicketMessage.description);
        state = PlanningSessionState.VOTING;
        sendCurrentStateToAll();
    }

    private void executeRevoteCommand() {
        if(state != PlanningSessionState.VOTING_FINISHED || ticket == null) {
            return;
        }
        ticket.resetTicketVotes();
        state = PlanningSessionState.VOTING;
        sendCurrentStateToAll();
    }

    private void executeFinishVotingCommand() {
        state = PlanningSessionState.VOTING_FINISHED;
        sendCurrentStateToAll();
    }

    private void executeRemoveParticipantCommand(PlanningHostCommandReceive command) {
        PlanningRemoveParticipantMessage removeParticipantMessage = hostCommandParser.parseRemoveParticipantMessage(command.message);
        Optional<PlanningUser> user = getUser(removeParticipantMessage.participantId);
        
        if(!user.isPresent()) {
            return;
        }
        PlanningJoinCommandSend removeCommand = new PlanningJoinCommandSend(PlanningJoinCommandSendType.REMOVE_PARTICIPANT, null);
        BinaryMessage message = joinCommandParser.parseCommandToBinaryMessage(removeCommand);
        webSocketHandler.sendMessage(user.get().webSocketSessionId, message);
        webSocketHandler.closeConnections(user.get().webSocketSessionId);
        removeUser(user.get());
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