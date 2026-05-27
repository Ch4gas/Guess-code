package guesscode.client;

import guesscode.api.GuessCodeService;
import guesscode.model.GameSnapshot;
import guesscode.model.GuessCodeException;
import guesscode.model.GuessResult;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class GuessCodeClient {
    private static final int PORT = 1099;
    private static final String SERVICE_NAME = "GuessCodeService";
    private static final int WAIT_TIME_MS = 1500;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";

        try (Scanner scanner = new Scanner(System.in)) {
            GuessCodeService service = connect(host);
            int playerId = register(service, scanner);

            System.out.println("Voce entrou como Jogador " + playerId + ".");
            waitUntilSecondPlayerArrives(service, playerId);
            submitSecret(service, scanner, playerId);
            play(service, scanner, playerId);
        } catch (Exception exception) {
            System.err.println("Erro no cliente: " + exception.getMessage());
        }
    }

    private static GuessCodeService connect(String host) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, PORT);
        return (GuessCodeService) registry.lookup(SERVICE_NAME);
    }

    private static int register(GuessCodeService service, Scanner scanner) throws Exception {
        System.out.print("Digite seu nome: ");
        String name = scanner.nextLine();
        return service.registerPlayer(name);
    }

    private static void waitUntilSecondPlayerArrives(GuessCodeService service, int playerId) throws Exception {
        String lastMessage = "";

        while (true) {
            GameSnapshot snapshot = service.getSnapshot(playerId);
            if (!snapshot.getMessage().equals(lastMessage)) {
                System.out.println("Status: " + snapshot.getMessage());
                lastMessage = snapshot.getMessage();
            }

            if (snapshot.getPlayersConnected() == 2) {
                return;
            }

            sleep();
        }
    }

    private static void submitSecret(GuessCodeService service, Scanner scanner, int playerId) throws Exception {
        while (true) {
            System.out.print("Informe sua sequencia secreta de 4 digitos: ");
            String secret = scanner.nextLine().trim();

            try {
                service.submitSecret(playerId, secret);
                System.out.println("Sequencia cadastrada. Aguarde o inicio da partida.");
                return;
            } catch (GuessCodeException exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
    }

    private static void play(GuessCodeService service, Scanner scanner, int playerId) throws Exception {
        String lastMessage = "";
        int lastHistorySize = 0;

        while (true) {
            GameSnapshot snapshot = service.getSnapshot(playerId);
            printNewHistory(snapshot.getHistory(), lastHistorySize);
            lastHistorySize = snapshot.getHistory().size();

            if (!snapshot.getMessage().equals(lastMessage)) {
                System.out.println("Status: " + snapshot.getMessage());
                lastMessage = snapshot.getMessage();
            }

            if (snapshot.isFinished()) {
                System.out.println("Vencedor: " + snapshot.getWinnerName());
                return;
            }

            if (!snapshot.areBothSecretsReady() || !snapshot.isYourTurn()) {
                sleep();
                continue;
            }

            makeGuess(service, scanner, playerId);
        }
    }

    private static void makeGuess(GuessCodeService service, Scanner scanner, int playerId) throws Exception {
        while (true) {
            System.out.print("Digite seu palpite de 4 digitos: ");
            String guess = scanner.nextLine().trim();

            try {
                GuessResult result = service.makeGuess(playerId, guess);
                System.out.println(result.toDisplayText());
                return;
            } catch (GuessCodeException exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
    }

    private static void printNewHistory(List<String> history, int lastHistorySize) {
        for (int i = lastHistorySize; i < history.size(); i++) {
            System.out.println("Historico: " + history.get(i));
        }
    }

    private static void sleep() throws InterruptedException {
        Thread.sleep(WAIT_TIME_MS);
    }
}
