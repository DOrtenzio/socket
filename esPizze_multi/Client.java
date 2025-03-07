package esPizze_multi;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private Scanner scanner;
    private ListaPizze listaPizze;

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

            try {
                //CONNESSIONE
                System.out.println("Server >" + mapper.readValue(in.readLine(), Comando.class).getNomeDelComando());

                //MENU'
                int scelta;
                do {
                    System.out.println(
                            "\nScegli un'opzione:" +
                                    "\n1) Richiedi Lista Pizze" +
                                    "\n2) Invia Pizza" +
                                    "\n3) Concludi ordine"
                    );
                    scelta = Integer.parseInt(scanner.next());

                    switch (scelta) {
                        //LISTA PIZZE
                        case 1:
                            //RICHIESTA
                            System.out.println("Client >  Richiesta lista pizze");
                            inviaMessaggio(mapper.writeValueAsString(new Comando("getLista",0)));
                            //RISPOSTA
                            this.listaPizze = mapper.readValue(in.readLine(), ListaPizze.class);
                            System.out.println("Client > Lista pizze ricevuta: \n" + this.listaPizze);
                            break;

                        //SCELTA PIZZA
                        case 2:
                            if (this.listaPizze == null) { //Non sappiamo ancora la lista delle pizze
                                //RICHIESTA
                                System.out.println("Client > Pizze non disponibili, richiesta al server. Sorry bro!");
                                inviaMessaggio(mapper.writeValueAsString(new Comando("getLista",0)));
                                //RISPOSTA
                                this.listaPizze = mapper.readValue(in.readLine(), ListaPizze.class);
                                System.out.println("Client > Lista pizze aggiornata: " + this.listaPizze);
                            }
                            int indexPizzaScelta;
                            do {
                                System.out.println("Seleziona una pizza (inserisci l'indice corrispondente):");
                                System.out.println(this.listaPizze.listaScelta());
                                indexPizzaScelta = Integer.parseInt(scanner.next());

                                if (indexPizzaScelta >= 0 && indexPizzaScelta < listaPizze.getLength()) {
                                    //RICHIESTA
                                    inviaMessaggio(mapper.writeValueAsString(new Comando("getPizza_"+listaPizze.getPizzaIndice(indexPizzaScelta).trim(),0)));
                                    System.out.println("Client > Pizza ordinata: " + this.listaPizze.getPizzaIndice(indexPizzaScelta));
                                    //RISPOSTA
                                    String pizzaRicevuta=(mapper.readValue(in.readLine(), Comando.class)).getNomeDelComando();
                                    if (pizzaRicevuta.equalsIgnoreCase(listaPizze.getPizzaIndice(indexPizzaScelta)))
                                        System.out.println("Client > Conferma: Pizza ricevuta correttamente.");
                                    else {
                                        System.out.println("Client > Pizza errata ricevuta!");
                                        indexPizzaScelta=-1;
                                    }
                                } else {
                                    System.out.println("Client > Indice non valido, riprova.");
                                }
                            } while (indexPizzaScelta==-1);
                            break;
                        case 3:
                            //RICHIESTA
                            System.out.println("Client > Chiusura ordine");
                            inviaMessaggio(mapper.writeValueAsString(new Comando("bye",0)));
                            //RISPOSTA
                            System.out.println("Server > "+mapper.readValue(in.readLine(), Comando.class).getNomeDelComando());
                            break;
                        default:
                            System.err.println("[ERRORE] Opzione non valida, riprova.");
                    }
                } while (scelta != 3);
            } catch (Exception classNot) {
                System.err.println(classNot);
            }
        } catch (UnknownHostException unknownHost) {
            System.err.println("[ERRORE] Host sconosciuto");
        } catch (Exception ioException) {
            System.err.println(ioException);
        } finally {
            try {
                scanner.close();
                in.close();
                out.close();
                connessione.close();
            } catch (Exception ioException) {
                System.err.println(ioException);
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
