package pizzeria2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private Scanner scanner;
    private String [] listaPizze;

    private Socket connessione;
    private PrintWriter out;
    private BufferedReader in;
    ObjectMapper mapper = new ObjectMapper();

    public Client() {
        this.listaPizze = null;
        this.scanner =new Scanner(System.in);
    }

    public void run() {
        try {
            connessione = new Socket("localhost", 9999);
            System.out.println("Client > Connessione inviata");

            out = new PrintWriter(connessione.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(connessione.getInputStream()));

            Comando comandoCliente=null;
            Comando comandoServer=null;

            do {
                try {
                    String messaggio = in.readLine();
                    comandoServer = mapper.readValue(messaggio, Comando.class);
                    System.out.println("Server >" + comandoServer.getNomeDelComando());
                    int scelta;
                    do {
                        System.out.println("\nScegli un'opzione:" +
                                "\n1) Richiedi Lista Pizze" +
                                "\n2) Invia Pizza" +
                                "\n3) Concludi ordine");
                        scelta = Integer.parseInt(scanner.next());
                        switch (scelta) {
                            case 1:
                                System.out.println("Client >  Richiesta lista pizze");
                                comandoCliente=new Comando();
                                comandoCliente.setNomeDelComando("lista");
                                inviaMessaggio(mapper.writeValueAsString(comandoCliente));


                                this.listaPizze = (mapper.readValue(in.readLine(), Comando.class)).getNomeDelComando().split("-");
                                System.out.println("Client > Lista pizze ricevuta: \n" + Arrays.toString(this.listaPizze));
                                break;
                            case 2:
                                if (listaPizze == null) {
                                    System.out.println("Client > Pizze non disponibili, richiesta al server <--->");
                                    comandoCliente=new Comando();
                                    comandoCliente.setNomeDelComando("lista");
                                    inviaMessaggio(mapper.writeValueAsString(comandoCliente));

                                    this.listaPizze = (mapper.readValue(in.readLine(), Comando.class)).getNomeDelComando().split("-");
                                    System.out.println("Client > Lista pizze aggiornata: " + Arrays.toString(this.listaPizze));
                                }
                                int indicePizza;
                                do {
                                    System.out.println("Seleziona una pizza (inserisci l'indice corrispondente):");
                                    for (int i = 0; i < listaPizze.length; i++) {
                                        System.out.println(i + ": " + listaPizze[i]);
                                    }
                                    indicePizza = Integer.parseInt(scanner.next());
                                    if (indicePizza >= 0 && indicePizza < listaPizze.length) {
                                        comandoCliente=new Comando();
                                        comandoCliente.setNomeDelComando(listaPizze[indicePizza].trim());
                                        inviaMessaggio(mapper.writeValueAsString(comandoCliente));
                                        System.out.println("Client > Pizza ordinata: " + listaPizze[indicePizza]);
                                        String pizzaRicevuta = (mapper.readValue(in.readLine(), Comando.class)).getNomeDelComando();
                                        if (pizzaRicevuta.equalsIgnoreCase(listaPizze[indicePizza])) {
                                            System.out.println("Client > Conferma: Pizza ricevuta correttamente.");
                                        } else {
                                            System.out.println("Client > Pizza errata ricevuta!");
                                        }
                                    } else {
                                        System.out.println("Client > Indice non valido, riprova.");
                                        indicePizza = -1;
                                    }
                                } while (indicePizza == -1);
                                break;
                            case 3:
                                System.out.println("Client > Chiusura ordine");
                                comandoCliente=new Comando();
                                comandoCliente.setNomeDelComando("FINE");
                                inviaMessaggio(mapper.writeValueAsString(comandoCliente));
                                System.out.println("Server > Ordine concluso con successo.");
                                scanner.close();
                                break;
                            default:
                                System.out.println("[ERRORE] Opzione non valida, riprova.");
                        }
                    } while (scelta != 3);
                } catch (Exception classNot) {
                    System.err.println("[ERRORE] Dati non in JSON");
                }
            } while (comandoServer!=null && !comandoServer.getNomeDelComando().equals("FINE"));
        } catch (UnknownHostException unknownHost) {
            System.err.println("[ERRORE] Host sconosciuto");
        } catch (Exception ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                scanner.close();
                in.close();
                out.close();
                connessione.close();
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void inviaMessaggio(String msg) {
        try {
            PrintWriter pw = new PrintWriter(out);
            pw.println(msg);
            pw.flush();
            System.out.println("JSON: " + msg);
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Client client = new Client();
        client.run();
    }
}
