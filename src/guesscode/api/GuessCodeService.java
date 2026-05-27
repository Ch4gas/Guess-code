package guesscode.api;

import guesscode.model.GameSnapshot;
import guesscode.model.GuessCodeException;
import guesscode.model.GuessResult;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GuessCodeService extends Remote {
    int registerPlayer(String name) throws RemoteException, GuessCodeException;

    void submitSecret(int playerId, String secret) throws RemoteException, GuessCodeException;

    GuessResult makeGuess(int playerId, String guess) throws RemoteException, GuessCodeException;

    GameSnapshot getSnapshot(int playerId) throws RemoteException, GuessCodeException;
}
