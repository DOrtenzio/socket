import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

class Command {
    private String commandName;
    private int par1;

    public String getCommandName() {
        return commandName;
    }

    public int getPar1() {
        return par1;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void setPar1(int par1) {
        this.par1 = par1;
    }
}

public class Client_json {

    private Socket requestSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String message;
    ObjectMapper mapper = new ObjectMapper();

    Client_json() {
    }

    void run() {
        try {
            //1. creating a socket to connect to the server
            requestSocket = new Socket("localhost", 9999);
            System.out.println("Connected to localhost in port");
            //2. get Input and Output streams
            out = new PrintWriter(requestSocket.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
            Command objClient=null;
            Command objServer=null;
            //3: Communicating with the server
            do {
                try {
                    message = in.readLine();
                    objServer = mapper.readValue(message, Command.class);
                    System.out.println("server command>" + objServer.getCommandName());
                    objClient=new Command();
                    objClient.setCommandName("bye");
                    sendMessage(mapper.writeValueAsString(objClient));
                } catch (Exception classNot) {
                    System.err.println("data received in unknown format");
                }
            } while (objServer!=null&&!objServer.getCommandName().equals("bye"));
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (Exception ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage(String msg) {
        try {


            PrintWriter pw = new PrintWriter(out);
            pw.println(msg);
            pw.flush();
            System.out.println("client>" + msg);
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Client_json client = new Client_json();
        client.run();
    }
}
