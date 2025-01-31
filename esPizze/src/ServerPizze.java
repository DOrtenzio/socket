import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ServerPizze {

    private String [] listaPizze;
    private ServerSocket providerSocket;
    private Socket connessioneConIlCliente;
    private ObjectOutputStream messaggiInOutput;
    private ObjectInputStream messaggiInInput;

    public ServerPizze(String [] listaPizze){
        this.listaPizze = listaPizze;
        this.connessioneConIlCliente = null;
    }

    private void inviaMessaggio(String messaggioDaInviare) {
        try {
            messaggiInOutput.writeObject(messaggioDaInviare);
            messaggiInOutput.flush();
            System.out.println("[SERVER] Messaggio inviato: " + messaggioDaInviare);
        } catch (IOException ioException) {
            System.err.println("[SERVER] Errore nell'invio del messaggio: " + messaggioDaInviare);
            ioException.printStackTrace();
        }
    }

    private String listaDellePizze(){
        return String.join("-", listaPizze);
    }

    private boolean cercaPizza(String s){
        for (String pizza : listaPizze){
            if (pizza.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public void inizializzazione(){
        try {
            providerSocket = new ServerSocket(9999, 10);
            System.out.println("[SERVER] In attesa di un cliente...");
        } catch (IOException e) {
            throw new RuntimeException("[SERVER] Errore nell'avvio del server", e);
        }
    }

    public void run(){
        try {
            connessioneConIlCliente = providerSocket.accept();
            System.out.println("[SERVER] Connessione ricevuta da: " + connessioneConIlCliente.getInetAddress().getHostName());

            messaggiInOutput = new ObjectOutputStream(connessioneConIlCliente.getOutputStream());
            messaggiInOutput.flush();
            messaggiInInput = new ObjectInputStream(connessioneConIlCliente.getInputStream());

            String messaggioRicevutoClient;
            do {
                try {
                    messaggioRicevutoClient = (String) messaggiInInput.readObject();
                    System.out.println("[CLIENT] " + messaggioRicevutoClient);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("[SERVER] Errore nella lettura del messaggio dal client", e);
                }

                if (messaggioRicevutoClient.equalsIgnoreCase("lista")) {
                    System.out.println("[SERVER] Inviando lista delle pizze...");
                    inviaMessaggio(listaDellePizze());
                } else if (cercaPizza(messaggioRicevutoClient)) {
                    System.out.println("[SERVER] Ordine ricevuto: " + messaggioRicevutoClient);
                    try {
                        System.out.println("[SERVER] Preparazione in corso...");
                        TimeUnit.SECONDS.sleep(4);
                        inviaMessaggio(messaggioRicevutoClient.trim());
                        System.out.println("[SERVER] Pizza consegnata: " + messaggioRicevutoClient);
                    } catch (InterruptedException e) {
                        throw new RuntimeException("[SERVER] Errore nella preparazione della pizza", e);
                    }
                } else {
                    System.out.println("[SERVER] Comando non riconosciuto: " + messaggioRicevutoClient);
                }

            } while (!messaggioRicevutoClient.equalsIgnoreCase("Fine"));

            System.out.println("[SERVER] Ordine terminato. In attesa di un nuovo cliente...");
        } catch (IOException e) {
            throw new RuntimeException("[SERVER] Errore nella gestione del client", e);
        }
    }

    public static void main(String[] args) {
        String [] selezionePizze = {"Margherita", "Marinara", "Carbonara", "Diavola", "Mari e monti"};
        ServerPizze pizzeria = new ServerPizze(selezionePizze);
        pizzeria.inizializzazione();
        while (true) {
            pizzeria.run();
        }
    }
}