package pizzeria2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Server {

    private ListaPizze listaPizze;

    private ServerSocket serverSocket;
    private Socket connessione;
    private PrintWriter out;
    private BufferedReader in;
    ObjectMapper mapper = new ObjectMapper();

    public Server(String [] listaPizze) {
        this.listaPizze=new ListaPizze(listaPizze);
        this.connessione = null;

        try {
            serverSocket = new ServerSocket(9999, 10);
            System.out.println("Server > ASPETTANDO LA CONNESSIONE");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            //INSTAURO UNA CONNESSIONE
            connessione = serverSocket.accept();
            System.out.println("Server > CONNESSO CON " + connessione.getInetAddress().getHostName());
            out = new PrintWriter(connessione.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(connessione.getInputStream()));

            //CONFERMA DI CONNESSIONE
            inviaMessaggio(mapper.writeValueAsString(new Comando("Connessione confermata",0)));

            Comando fromClient=null;
            do {
                try {
                    fromClient = mapper.readValue(in.readLine(), Comando.class);
                    System.out.println("Client >" + fromClient.getNomeDelComando());

                    if (fromClient.getNomeDelComando().equals("bye")) {
                        inviaMessaggio(mapper.writeValueAsString(new Comando("bye",0)));
                    }else if (fromClient.getNomeDelComando().equalsIgnoreCase("getLista")) {
                        System.out.println("Server > Inviando lista delle pizze...");
                        inviaMessaggio(mapper.writeValueAsString(this.listaPizze));

                    } else if (listaPizze.cercaPizza(fromClient.getNomeDelComando().trim().split("_")[1])) { //Dal comando getPizza_NomePizza isolo NomePizza
                        //RICHIESTA
                        System.out.println("Server > Ordine ricevuto: " + fromClient.getNomeDelComando());
                        System.out.println("Server > Preparazione in corso...");
                        //RISPOSTA
                        TimeUnit.SECONDS.sleep(4);
                        inviaMessaggio(mapper.writeValueAsString(new Comando(fromClient.getNomeDelComando().trim().split("_")[1],0)));
                        System.out.println("Server > Pizza consegnata: " + fromClient.getNomeDelComando().trim().split("_")[1]);
                    } else {
                        System.out.println("Server " + fromClient.getNomeDelComando());
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            } while (fromClient!=null && !fromClient.getNomeDelComando().equals("bye"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                connessione.close();
                out.close();
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
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
        String [] selezionePizze = {"Margherita", "Marinara", "Carbonara", "Diavola", "Mari e monti"};
        Server server = new Server(selezionePizze);
        while (true) {
            server.run();
        }
    }

}
