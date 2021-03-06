import java.io.*;
import java.net.*;

class StagServer
{
    private static StagGame stagGame;
    public static void main(String args[])
    {
        if(args.length != 2) System.out.println("Usage: java StagServer <entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);
    }

    public StagServer(String entityFilename, String actionFilename, int portNumber)
    {
        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            stagGame = new StagGame(entityFilename, actionFilename); //parse files and initialize the game entities
            while(true) acceptNextConnection(ss); 
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss)
    {
        try {
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            processNextCommand(in, out);
            out.close();
            in.close();
            socket.close();
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextCommand(BufferedReader in, BufferedWriter out) throws IOException
    {
        String line = in.readLine();
        try { // process incoming commands wtih the Controller class instance (it outputs result string after the process)
            StagController controller = new StagController(stagGame, line); 
            out.write(controller.toString());
        } catch (StagCommandException se) {
            out.write(se.toString());
        }
    }

}
