package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    static String ip = "localhost"; //138.68.68.132 @myCPHBusiness
    static int port = 7777;
        
        public static void main(String[] args) throws IOException
    {
        if (args.length == 2)
        {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }

        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(ip, port));
        System.out.println("Server started\nPort: " + port + "\nIP: " + ip);
        
        while(true)
        {
            Socket socket = ss.accept();
            System.out.println("New client connected");
            ClientThread ct = new ClientThread(socket);
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.execute(ct);
            //ct.start();
        }
    }
}
