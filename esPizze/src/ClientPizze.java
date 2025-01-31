import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class ClientPizze {
    private Scanner in = new Scanner(System.in);
    private Socket connessioneConLaPizzeria;
    private ObjectOutputStream invio;
    private ObjectInputStream ricevi;
    private String[] listaPizze;

    public ClientPizze() {
        this.listaPizze = null;
    }

    public void run() {
        try {
            System.out.println("[LOG] Connessione al server in corso...");
            connessioneConLaPizzeria = new Socket("localhost", 9999);
            invio = new ObjectOutputStream(connessioneConLaPizzeria.getOutputStream());
            invio.flush();
            ricevi = new ObjectInputStream(connessioneConLaPizzeria.getInputStream());
            System.out.println("[LOG] Connessione stabilita con il server.");

            int scelta = 0;
            do {
                try {
                    System.out.println("\nScegli un'opzione:" +
                            "\n1) Richiedi Lista Pizze" +
                            "\n2) Invia Pizza" +
                            "\n3) Concludi ordine");
                    scelta = Integer.parseInt(in.next());
                    switch (scelta) {
                        case 1:
                            System.out.println("[LOG] Richiesta lista pizze...");
                            sendMessage("lista");
                            listaPizze = ((String) (ricevi.readObject())).split("-");
                            System.out.println("[LOG] Lista pizze ricevuta: \n" + Arrays.toString(listaPizze));
                            break;
                        case 2:
                            if (listaPizze == null) {
                                System.out.println("[LOG] Pizze non disponibili, richiesta al server...");
                                sendMessage("lista");
                                listaPizze = ((String) (ricevi.readObject())).split("-");
                                System.out.println("[LOG] Lista pizze aggiornata: " + Arrays.toString(listaPizze));
                            }
                            int indice;
                            do {
                                System.out.println("Seleziona una pizza (inserisci l'indice corrispondente):");
                                for (int i = 0; i < listaPizze.length; i++) {
                                    System.out.println(i + ": " + listaPizze[i]);
                                }
                                indice = Integer.parseInt(in.next());

                                if (indice >= 0 && indice < listaPizze.length) {
                                    sendMessage(listaPizze[indice].trim());
                                    System.out.println("[LOG] Pizza ordinata: " + listaPizze[indice]);
                                    String risposta = (String) ricevi.readObject();
                                    if (risposta.equalsIgnoreCase(listaPizze[indice])) {
                                        System.out.println("[LOG] Conferma: Pizza ricevuta correttamente.");
                                    } else {
                                        System.out.println("[ERRORE] Pizza errata ricevuta!");
                                    }
                                } else {
                                    System.out.println("[ERRORE] Indice non valido, riprova.");
                                    indice = -1;
                                }
                            } while (indice == -1);
                            break;
                        case 3:
                            System.out.println("[LOG] Chiusura ordine...");
                            sendMessage("FINE");
                            System.out.println("[LOG] Ordine concluso con successo.");
                            in.close();
                            break;
                        default:
                            System.out.println("[ERRORE] Opzione non valida, riprova.");
                    }
                } catch (ClassNotFoundException classNot) {
                    System.out.println("[ERRORE] Dati ricevuti in formato errato.");
                }
            } while (scelta != 3);
        } catch (UnknownHostException unknownHost) {
            System.out.println("[ERRORE] Host sconosciuto, impossibile connettersi.");
        } catch (IOException ioException) {
            System.out.println("[ERRORE] Errore di comunicazione con il server.");
        } finally {
            try {
                if (invio != null) invio.close();
                if (ricevi != null) ricevi.close();
                if (connessioneConLaPizzeria != null) connessioneConLaPizzeria.close();
                System.out.println("[LOG] Connessione chiusa.");
            } catch (IOException ioException) {
                System.out.println("[ERRORE] Errore durante la chiusura della connessione.");
            }
        }
    }

    private void sendMessage(String msg) {
        try {
            invio.writeObject(msg);
            invio.flush();
            System.out.println("[LOG] Messaggio inviato al server: " + msg);
        } catch (IOException ioException) {
            System.out.println("[ERRORE] Errore nell'invio del messaggio.");
        }
    }

    public static void main(String[] args) {
        ClientPizze cliente = new ClientPizze();
        cliente.run();
    }
}