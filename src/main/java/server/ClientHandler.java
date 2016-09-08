package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
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
    //static DateFormat dateFormat = new SimpleDateFormat("EEEEEEEEE dd. LLLLLLLL yyyy HH:mm:ss zzz");
    //static Date date = new Date();

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
            //print.println("Connected - " + dateFormat.format(date));
            String msg = scan.nextLine();
            String[] input = msg.split(":");
            if (!input[0].equals("LOGIN"))
            {
                scan.close();
                print.close();
                socket.close();
                System.out.println("Client disconnected");
                return;
            } else
            {
                server.addUser(msg, this);
            }
            while (!msg.equals("LOGOUT:"))
            {
                msg = scan.nextLine();
                input = msg.split(":");
                switch (input[0])
                {
                    case "MSG":
                        server.sendMsg(msg, this);
                        break;
                }
            }
            server.logout(this);
        } catch (IOException | NoSuchElementException ex)
        {
            Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE, null, ex);
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            server.logout(this);
        }
    }

    public void post(String msg)
    {
        this.print.println(msg);
        this.print.flush();
    }
}
