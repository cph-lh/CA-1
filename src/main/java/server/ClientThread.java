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

public class ClientThread extends Thread
{

    protected Socket socket;
    static DateFormat dateFormat = new SimpleDateFormat("EEEEEEEEE dd. LLLLLLLL yyyy HH:mm:ss zzz");
    static Date date = new Date();

    public ClientThread(Socket s)
    {
        this.socket = s;
    }

    @Override
    public void run()
    {
        try
        {
            Scanner scan = new Scanner(socket.getInputStream());
            PrintWriter print = new PrintWriter(socket.getOutputStream(), true);
            String msg = "";
            print.println("Connected - "+dateFormat.format(date));
            while (true)
            {
                msg = scan.nextLine();
                if (msg.equals("LOGOUT"))
                {
                    scan.close();
                    print.close();
                    socket.close();
                    System.out.println("Client disconnected");
                    break;
                }
                else{
                    print.println(msg.toUpperCase());
                }            
            }
        } catch (IOException ex)
        {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
