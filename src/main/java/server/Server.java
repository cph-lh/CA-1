package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server
{

    static String ip = "localhost"; //138.68.68.132
    static int port = 7777;
    ArrayList<ClientHandler> cList = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {
        if (args.length == 2)
        {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }

        Server server = new Server();
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket ss = new ServerSocket();
        Log.setLogFile("logFile.txt", "ServerLog");
        ss.bind(new InetSocketAddress(ip, port));
        System.out.println("Server started\nPort: " + port + "\nIP: " + ip);
        executor.execute(()
                -> 
                {
                    while (true)
                    {
                        try
                        {
                            Socket socket = ss.accept();
                            ClientHandler ch = new ClientHandler(socket, server);
                            executor.execute(ch);
                            System.out.println("New client connected");
                        } catch (IOException ex)
                        {
                            Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE, null, ex);
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
        });
    }

    public void addUser(String msg, ClientHandler ch)
    {
        String[] ca = msg.split(":");
        ch.username = ca[1];
        cList.add(ch);
        Logger.getLogger(Log.LOG_NAME).log(Level.INFO, ca[1]+" logged in");
        printList();
    }

    public void sendMsg(String msg, ClientHandler ch)
    {
        String[] array = msg.split(":");
        if (array[1].isEmpty())
        {
            for (ClientHandler user : cList)
            {
                user.post("MSGRES:" + ch.username + ":" + array[2]);
            }
        } else
        {
            String[] list = array[1].split(",");
            for (ClientHandler user : cList)
            {
                for (String users : list)
                {
                    if (user.getUsername().equals(users))
                    {
                        user.post("MSGRES:" + ch.username + ":" + array[2]);
                    }
                }
            }
        }
    }

    public void printList()
    {
        String list = "CLIENTLIST:";
        for (ClientHandler user : cList)
        {
            list = list + user.getUsername() + ",";
        }
        list = list.substring(0, list.length() - 1);
        for (ClientHandler user : cList)
        {
            user.post(list);
        }
    }

    public void logout(ClientHandler ch)
    {
        try
        {
            ch.scan.close();
            ch.print.close();
            ch.socket.close();
            cList.remove(ch);
            printList();
            Logger.getLogger(Log.LOG_NAME).log(Level.INFO, ch.getUsername()+" logged out");
        } catch (IOException ex)
        {
            Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE, null, ex);
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
