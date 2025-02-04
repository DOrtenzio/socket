import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client_string {

    private Socket requestSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String message;

    Client_string() {
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
            //3: Communicating with the server
            do {
                try {
                    message = in.readLine();
                    System.out.println("server>" + message);
                    sendMessage("Hi my server");
                    message = "bye";
                    sendMessage(message);
                } catch (Exception classNot) {
                    System.err.println("data received in unknown format");
                }
            } while (message!=null&&!message.equals("bye"));
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
            out.println(msg);
            out.flush();
            System.out.println("client>" + msg);
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Client_string client = new Client_string();
        client.run();
    }
}
