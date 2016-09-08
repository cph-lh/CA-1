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
                            ClientHandler ct = new ClientHandler(socket, server);
                            executor.execute(ct);
                            System.out.println("New client connected");
//                            for (int i = 0; i < server.cList.size(); i++)
//                            {
//                                System.out.println(server.cList.get(i).getSocket());
//                            }
                        } catch (IOException ex)
                        {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
        });
    }
    
    public void addUser(String msg, ClientHandler ct)
    {
        String[] ca = msg.split(":");
        ct.username = ca[1];
        cList.add(ct);
        String list = "Connected users: ";   
        
        for (ClientHandler user : cList)
        {
            list = list + user.getUsername() + ",";
        }
        
        list = list.substring(0, list.length() - 1);
        for(ClientHandler user : cList)
        {
            user.post(list);
        }
    }
    
    public void sendMsg(String msg, ClientHandler ct)
    {
        String[] array = msg.split(":");
        if (array[1].isEmpty())
        {
            for (ClientHandler user : cList)
            {
                user.post("MSGRES:" + ct.username + ":" + array[2]);
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
                        user.post("MSGRES:" + ct.username + ":" + array[2]);
                    }
                }
            }
        }
    }    
}
