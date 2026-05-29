package guesscode.model;

import java.io.Serializable;



public class GuessResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String guess;
    private final int correctPosition;
    private final int correctNumberWrongPosition;
    private final String correctNumberWrongPositionDigits;
    private final int incorrect;
    private final boolean won;
    private final int nextTurnPlayerId;
    private final String message;

    public GuessResult(
            String guess,
            int correctPosition,
            int correctNumberWrongPosition,
            String correctNumberWrongPositionDigits,
            int incorrect,
            boolean won,
            int nextTurnPlayerId,
            String message
    ) {
        this.guess = guess;
        this.correctPosition = correctPosition;
        this.correctNumberWrongPosition = correctNumberWrongPosition;
        this.correctNumberWrongPositionDigits = correctNumberWrongPositionDigits == null ? "" : correctNumberWrongPositionDigits;
        this.incorrect = incorrect;
        this.won = won;
        this.nextTurnPlayerId = nextTurnPlayerId;
        this.message = message;
    }

    public String getGuess() {
        return guess;
    }

    public int getCorrectPosition() {
        return correctPosition;
    }

    public int getCorrectNumberWrongPosition() {
        return correctNumberWrongPosition;
    }

    public String getCorrectNumberWrongPositionDigits() {
        return correctNumberWrongPositionDigits;
    }

    public int getIncorrect() {
        return incorrect;
    }

    public boolean hasWon() {
        return won;
    }

    public int getNextTurnPlayerId() {
        return nextTurnPlayerId;
    }

    public String getMessage() {
        return message;
    }

    public String toDisplayText() {
        String wrongPositionText = correctNumberWrongPosition == 0
                ? "nenhum"
                : correctNumberWrongPosition + " (" + correctNumberWrongPositionDigits + ")";
        String wrongPositionLabel = correctNumberWrongPosition == 1
                ? "Numero certo na posicao errada"
                : "Numeros certos na posicao errada";

        return "Palpite " + guess
                + " | Posicao certa: " + correctPosition
                + " | " + wrongPositionLabel + ": " + wrongPositionText
                + " | Incorretos: " + incorrect
                + " | " + message;
    }
}
