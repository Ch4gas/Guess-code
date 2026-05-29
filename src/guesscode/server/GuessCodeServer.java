package guesscode.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class GuessCodeServer {

    private static final int PORT = 1099;
    private static final String SERVICE_NAME = "GuessCodeService";

    public static void main(String[] args) {

        try {

            System.setProperty(
                    "java.rmi.server.hostname",
                    "10.8.185.12"
            );

            Registry registry = createOrFindRegistry();

            registry.rebind(
                    SERVICE_NAME,
                    new GuessCodeServiceImpl()
            );

            System.out.println("Servidor GuessCode iniciado.");
            System.out.println("Servico RMI: " + SERVICE_NAME);
            System.out.println("Porta: " + PORT);

        } catch (Exception exception) {

            System.err.println(
                    "Erro ao iniciar o servidor: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }

    private static Registry createOrFindRegistry()
            throws Exception {

        try {

            return LocateRegistry.createRegistry(PORT);

        } catch (ExportException exception) {

            return LocateRegistry.getRegistry(PORT);
        }
    }
}