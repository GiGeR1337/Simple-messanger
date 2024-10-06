import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;


    public Client(Socket socket, String username) {
        try{
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEveryThing(socket, br, bw);
        }
    }
    public void sentMessage(){
        try{
            bw.write(username);
            bw.newLine();
            bw.flush();

            Scanner scan = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = scan.nextLine();
                bw.write(username + ": " + messageToSend);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            closeEveryThing(socket, br, bw);
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;

                while(socket.isConnected()){
                    try{
                        messageFromGroupChat = br.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeEveryThing(socket, br, bw);
                    }
                }
            }
        }).start(); //waiting for messages
    }

    private void closeEveryThing(Socket socket, BufferedReader br, BufferedWriter bw) {
        try{
            if(br != null)
                br.close();

            if(bw != null)
                bw.close();

            if(socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scan.nextLine();
        Socket socket = new Socket("localhost", 9999);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sentMessage();
    }
}
