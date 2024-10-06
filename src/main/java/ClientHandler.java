import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = br.readLine(); //waiting for sentMessage (Client)
            clientHandlers.add(this); //represents new client
            broadcastMessage("SERVER: " + clientUsername + "joined the chat!");
        } catch (IOException e) {
            closeEveryThing(socket, br, bw);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = br.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e){
                closeEveryThing(socket, br, bw);
                break;
            }
        }
    }

    //used to send the message client wrote to everyone else
    public void broadcastMessage(String messageToSend) {
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bw.write(messageToSend);
                    clientHandler.bw.newLine();
                    clientHandler.bw.flush(); // 22:50
                }
            } catch (IOException e) {
                closeEveryThing(socket, br, bw);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this); //current clientHandler
        broadcastMessage("SERVER: " + clientUsername + " has left the chat");
    }

    public void closeEveryThing(Socket socket, BufferedReader br, BufferedWriter bw) {
        removeClientHandler();
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
}
