package esPizze_multi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private ListaPizze listaPizze;

    public Server() {
        try {
            String [] selezionePizze = {"Margherita", "Marinara", "Carbonara", "Diavola", "Mari e monti"};
            this.listaPizze=new ListaPizze(selezionePizze);
            //Connessione in attesa
            serverSocket = new ServerSocket(9999, 10);
            System.out.println("Server > ASPETTANDO LA CONNESSIONE");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        try {
            Socket connessione = serverSocket.accept();
            GestoreClienti gc=new GestoreClienti(connessione,this.listaPizze);
            Thread t = new Thread(gc);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Server server=new Server();
        while (true) server.run();
    }
}
