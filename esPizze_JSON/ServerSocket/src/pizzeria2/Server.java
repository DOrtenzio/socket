package pizzeria2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Server {

    private String [] listaPizze;

    private ServerSocket serverSocket;
    private Socket connessione;
    private PrintWriter out;
    private BufferedReader in;
    ObjectMapper mapper = new ObjectMapper();

    Server(String [] listaPizze) {
        this.listaPizze=listaPizze;
        this.connessione = null;
    }

    //METODI COMUNICAZIONE ED INSTAURAZIONE DI ESSA

    public void initial(){
        try {
            serverSocket = new ServerSocket(9999, 10);
            System.out.println("Server > ASPETTANDO LA CONNESSIONE");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void inviaMessaggio(String msg) {
        try {
            PrintWriter pw = new PrintWriter(out);
            pw.println(msg);
            pw.flush();
            System.out.println("Server > " + msg);
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

    public void run() {
        try {
            connessione = serverSocket.accept();
            System.out.println("Server > CONNESSO CON " + connessione.getInetAddress().getHostName());
            //3. Ottenendo Input e Output
            out = new PrintWriter(connessione.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(connessione.getInputStream()));


            Comando comandoServer=new Comando();
            comandoServer.setNomeDelComando("Connessione confermata");
            inviaMessaggio(mapper.writeValueAsString(comandoServer));

            Comando comandoClient=null;
            do {
                try {
					String messaggioRicevuto=in.readLine();
                    comandoClient = mapper.readValue(messaggioRicevuto, Comando.class);
                    System.out.println("Client >" + comandoClient.getNomeDelComando());


                    if (comandoClient.getNomeDelComando().equals("FINE")) {
                        inviaMessaggio(mapper.writeValueAsString(comandoClient)); //Riinvio fine
                    }else if (comandoClient.getNomeDelComando().equalsIgnoreCase("lista")) {
                        System.out.println("Server > Inviando lista delle pizze...");
                        comandoServer.setNomeDelComando(listaDellePizze());
                        inviaMessaggio(mapper.writeValueAsString(comandoServer));
                    } else if (cercaPizza(comandoClient.getNomeDelComando())) {
                        System.out.println("Server > Ordine ricevuto: " + comandoClient.getNomeDelComando());
                        try {
                            System.out.println("Server > Preparazione in corso...");
                            TimeUnit.SECONDS.sleep(4);
                            inviaMessaggio(mapper.writeValueAsString(comandoClient));
                            System.out.println("Server > Pizza consegnata: " + comandoClient.getNomeDelComando());
                        } catch (InterruptedException e) {
                            throw new RuntimeException("Server > Errore nella preparazione della pizza", e);
                        }
                    } else {
                        System.out.println("Server > Comando non riconosciuto: " + comandoClient.getNomeDelComando());
                    }

                } catch (Exception e) {
                    System.err.println(e);
                }
            } while (comandoClient!=null && !comandoClient.getNomeDelComando().equals("FINE"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    //METODI GESTIONE LISTA E PIZZE
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

    public static void main(String args[]) {
        String [] selezionePizze = {"Margherita", "Marinara", "Carbonara", "Diavola", "Mari e monti"};
        Server server = new Server(selezionePizze);
        server.initial();
        while (true) {
            server.run();
        }
    }

}
