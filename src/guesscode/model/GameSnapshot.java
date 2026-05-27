package guesscode.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int playerId;
    private final String playerName;
    private final String opponentName;
    private final int playersConnected;
    private final boolean ownSecretReady;
    private final boolean bothSecretsReady;
    private final int currentTurnPlayerId;
    private final boolean yourTurn;
    private final boolean finished;
    private final String winnerName;
    private final String message;
    private final List<String> history;

    public GameSnapshot(
            int playerId,
            String playerName,
            String opponentName,
            int playersConnected,
            boolean ownSecretReady,
            boolean bothSecretsReady,
            int currentTurnPlayerId,
            boolean yourTurn,
            boolean finished,
            String winnerName,
            String message,
            List<String> history
    ) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.opponentName = opponentName;
        this.playersConnected = playersConnected;
        this.ownSecretReady = ownSecretReady;
        this.bothSecretsReady = bothSecretsReady;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.yourTurn = yourTurn;
        this.finished = finished;
        this.winnerName = winnerName;
        this.message = message;
        this.history = Collections.unmodifiableList(new ArrayList<>(history));
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }

    public boolean isOwnSecretReady() {
        return ownSecretReady;
    }

    public boolean areBothSecretsReady() {
        return bothSecretsReady;
    }

    public int getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getHistory() {
        return history;
    }
}
