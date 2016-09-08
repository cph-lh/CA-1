package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread
{

    protected Socket socket;
    protected Server server;
    String username = "";
    Scanner scan;
    PrintWriter print;
    static DateFormat dateFormat = new SimpleDateFormat("EEEEEEEEE dd. LLLLLLLL yyyy HH:mm:ss zzz");
    static Date date = new Date();

    public ClientHandler(Socket socket, Server server) throws IOException
    {
        this.socket = socket;
        this.server = server;
        scan = new Scanner(socket.getInputStream());
        print = new PrintWriter(socket.getOutputStream(), true);
    }

    public Socket getSocket()
    {
        return socket;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public void run()
    {
        try
        {
            print.println("Connected - " + dateFormat.format(date));
            String msg = scan.nextLine();
            String[] input = msg.split(":");
            if (!input[0].equals("LOGIN"))
            {
                scan.close();
                print.close();
                socket.close();
                return;
            } else
            {
                server.addUser(msg, this);
            }
            while (!msg.equals("LOGOUT"))
            {
                msg = scan.nextLine();
                input = msg.split(":");
                switch (input[0])
                {
                    case "MSG":
                        server.sendMsg(msg, this);
                        break;
                    case "LOGOUT":
                        scan.close();
                        print.close();
                        socket.close();
                        server.cList.remove(this);
                        String list = "Connected users: ";

                        for (ClientHandler user : server.cList)
                        {
                            list = list + user.getUsername() + ",";
                        }

                        list = list.substring(0, list.length() - 1);
                        for (ClientHandler user : server.cList)
                        {
                            user.post(list);
                        }
                        System.out.println("Client disconnected");
                        break;
                }
            }
        } catch (IOException ex)
        {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void post(String msg)
    {
        this.print.println(msg);
        this.print.flush();
    }
}
