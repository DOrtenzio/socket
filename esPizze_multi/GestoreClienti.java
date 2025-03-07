package esPizze_multi;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class GestoreClienti implements Runnable{
    private ListaPizze listaPizze;

    private Socket connessione;
    private PrintWriter out;
    private BufferedReader in;
    ObjectMapper mapper = new ObjectMapper();

    public GestoreClienti(Socket connessione, ListaPizze listaPizze){
        this.connessione=connessione;
        this.listaPizze=listaPizze;
    }

    public void run() {
        try {
            System.out.println("Server > CONNESSO CON " + this.connessione.getInetAddress().getHostName());
            out = new PrintWriter(this.connessione.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(this.connessione.getInputStream()));

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
}
