package guesscode.model;

import java.io.Serializable;

public class GuessResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String guess;
    private final int correctPosition;
    private final int correctNumberWrongPosition;
    private final int incorrect;
    private final boolean won;
    private final int nextTurnPlayerId;
    private final String message;

    public GuessResult(
            String guess,
            int correctPosition,
            int correctNumberWrongPosition,
            int incorrect,
            boolean won,
            int nextTurnPlayerId,
            String message
    ) {
        this.guess = guess;
        this.correctPosition = correctPosition;
        this.correctNumberWrongPosition = correctNumberWrongPosition;
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
        return "Palpite " + guess
                + " | Posicao certa: " + correctPosition
                + " | Numero certo na posicao errada: " + correctNumberWrongPosition
                + " | Incorretos: " + incorrect
                + " | " + message;
    }
}
