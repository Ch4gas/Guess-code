package guesscode.server;

import guesscode.api.GuessCodeService;
import guesscode.model.GameSnapshot;
import guesscode.model.GuessCodeException;
import guesscode.model.GuessResult;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class GuessCodeServiceImpl extends UnicastRemoteObject implements GuessCodeService {
    private static final long serialVersionUID = 1L;
    private static final int CODE_LENGTH = 4;
    private static final int MAX_PLAYERS = 2;

    private final Map<Integer, Player> players = new LinkedHashMap<>();
    private int nextPlayerId = 1;
    private int currentTurnPlayerId = 1;
    private boolean finished;
    private Integer winnerId;

    public GuessCodeServiceImpl() throws RemoteException {
        super(1100);
    }

    @Override
    public synchronized int registerPlayer(String name) throws GuessCodeException {
        if (players.size() >= MAX_PLAYERS) {
            throw new GuessCodeException("A partida ja possui dois jogadores.");
        }

        String safeName = normalizeName(name);
        int id = nextPlayerId++;
        players.put(id, new Player(id, safeName));
        System.out.println("Jogador conectado: " + safeName + " (ID " + id + ")");
        return id;
    }

    @Override
    public synchronized void submitSecret(int playerId, String secret) throws GuessCodeException {
        Player player = requirePlayer(playerId);
        validateCode(secret, "sequencia secreta");

        if (player.secret != null) {
            throw new GuessCodeException("Voce ja informou sua sequencia secreta.");
        }

        player.secret = secret;
        player.history.add("Sua sequencia secreta foi cadastrada.");

        if (areBothSecretsReady()) {
            currentTurnPlayerId = 1;
            addHistoryForAll("As duas sequencias foram cadastradas. Jogador 1 comeca.");
        }
    }

    @Override
    public synchronized GuessResult makeGuess(int playerId, String guess) throws GuessCodeException {
        Player player = requirePlayer(playerId);
        Player opponent = requireOpponent(playerId);
        validateCode(guess, "palpite");

        if (finished) {
            throw new GuessCodeException("A partida ja terminou.");
        }

        if (!areBothSecretsReady()) {
            throw new GuessCodeException("A partida so comeca depois que os dois jogadores informarem as sequencias.");
        }

        if (playerId != currentTurnPlayerId) {
            throw new GuessCodeException("Ainda nao e sua vez de jogar.");
        }

        GuessResult result = evaluateGuess(guess, opponent.secret);
        boolean won = result.getCorrectPosition() == CODE_LENGTH;

        if (won) {
            finished = true;
            winnerId = playerId;
            result = new GuessResult(
                    guess,
                    result.getCorrectPosition(),
                    result.getCorrectNumberWrongPosition(),
                    result.getCorrectNumberWrongPositionDigits(),
                    result.getIncorrect(),
                    true,
                    playerId,
                    "Voce acertou a sequencia e venceu!"
            );
        } else {
            currentTurnPlayerId = opponent.id;
            result = new GuessResult(
                    guess,
                    result.getCorrectPosition(),
                    result.getCorrectNumberWrongPosition(),
                    result.getCorrectNumberWrongPositionDigits(),
                    result.getIncorrect(),
                    false,
                    currentTurnPlayerId,
                    "Palpite registrado. Turno do adversario."
            );
        }

        player.history.add("Voce tentou " + result.toDisplayText());
        opponent.history.add(player.name + " tentou " + guess + " contra sua sequencia.");

        if (won) {
            addHistoryForAll(player.name + " venceu a partida.");
        }

        System.out.println(player.name + " tentou " + guess + " -> " + result.toDisplayText());
        return result;
    }

    @Override
    public synchronized GameSnapshot getSnapshot(int playerId) throws GuessCodeException {
        Player player = requirePlayer(playerId);
        Player opponent = findOpponent(playerId);
        boolean bothReady = areBothSecretsReady();
        String opponentName = opponent == null ? "Aguardando jogador" : opponent.name;
        String winnerName = winnerId == null ? "" : players.get(winnerId).name;

        return new GameSnapshot(
                player.id,
                player.name,
                opponentName,
                players.size(),
                player.secret != null,
                bothReady,
                bothReady ? currentTurnPlayerId : 0,
                bothReady && currentTurnPlayerId == playerId && !finished,
                finished,
                winnerName,
                buildMessage(player, opponent, bothReady),
                player.history
        );
    }

    private GuessResult evaluateGuess(String guess, String secret) {
        int correctPosition = 0;
        int[] secretCounts = new int[10];
        boolean[] matchedPositions = new boolean[CODE_LENGTH];

        for (int i = 0; i < CODE_LENGTH; i++) {
            char guessDigit = guess.charAt(i);
            char secretDigit = secret.charAt(i);

            if (guessDigit == secretDigit) {
                correctPosition++;
                matchedPositions[i] = true;
            } else {
                secretCounts[secretDigit - '0']++;
            }
        }

        List<Character> correctNumberWrongPositionDigits = new ArrayList<>();
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (matchedPositions[i]) {
                continue;
            }

            char guessDigit = guess.charAt(i);
            int digitIndex = guessDigit - '0';
            if (secretCounts[digitIndex] > 0) {
                correctNumberWrongPositionDigits.add(guessDigit);
                secretCounts[digitIndex]--;
            }
        }

        int correctNumberWrongPosition = correctNumberWrongPositionDigits.size();
        int incorrect = CODE_LENGTH - correctPosition - correctNumberWrongPosition;
        return new GuessResult(
                guess,
                correctPosition,
                correctNumberWrongPosition,
                formatDigits(correctNumberWrongPositionDigits),
                incorrect,
                false,
                currentTurnPlayerId,
                "Resultado calculado."
        );
    }

    private String formatDigits(List<Character> digits) {
        if (digits.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (Character digit : digits) {
            joiner.add(String.valueOf(digit));
        }
        return joiner.toString();
    }

    private String buildMessage(Player player, Player opponent, boolean bothReady) {
        if (finished) {
            return "Partida encerrada. Vencedor: " + players.get(winnerId).name + ".";
        }

        if (players.size() < MAX_PLAYERS) {
            return "Aguardando o segundo jogador entrar.";
        }

        if (player.secret == null) {
            return "Informe sua sequencia secreta.";
        }

        if (opponent != null && opponent.secret == null) {
            return "Aguardando o adversario informar a sequencia secreta.";
        }

        if (!bothReady) {
            return "Aguardando as sequencias secretas.";
        }

        if (currentTurnPlayerId == player.id) {
            return "Sua vez de jogar.";
        }

        return "Aguardando jogada de " + players.get(currentTurnPlayerId).name + ".";
    }

    private Player requirePlayer(int playerId) throws GuessCodeException {
        Player player = players.get(playerId);
        if (player == null) {
            throw new GuessCodeException("Jogador nao encontrado.");
        }
        return player;
    }

    private Player requireOpponent(int playerId) throws GuessCodeException {
        Player opponent = findOpponent(playerId);
        if (opponent == null) {
            throw new GuessCodeException("Aguardando outro jogador entrar.");
        }
        return opponent;
    }

    private Player findOpponent(int playerId) {
        for (Player player : players.values()) {
            if (player.id != playerId) {
                return player;
            }
        }
        return null;
    }

    private boolean areBothSecretsReady() {
        if (players.size() < MAX_PLAYERS) {
            return false;
        }

        for (Player player : players.values()) {
            if (player.secret == null) {
                return false;
            }
        }

        return true;
    }

    private void validateCode(String code, String fieldName) throws GuessCodeException {
        if (code == null || !code.matches("\\d{" + CODE_LENGTH + "}")) {
            throw new GuessCodeException("A " + fieldName + " deve ter exatamente " + CODE_LENGTH + " digitos.");
        }
    }

    private String normalizeName(String name) {
        String value = name == null ? "" : name.trim();
        if (value.isEmpty()) {
            return "Jogador " + nextPlayerId;
        }
        return value;
    }

    private void addHistoryForAll(String message) {
        for (Player player : players.values()) {
            player.history.add(message);
        }
    }

    private static class Player {
        private final int id;
        private final String name;
        private final List<String> history = new ArrayList<>();
        private String secret;

        private Player(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
