import java.io.*;
import java.net.*;
import java.util.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

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
            stagGame = new StagGame(entityFilename, actionFilename);
            while(true) acceptNextConnection(ss);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss)
    {
        try {
            // Next line will block until a connection is received
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            processNextCommand(in, out);
            // out.flush();
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
        out.write("You said... " + line + "\n");
        // // Process command
        // String[] incomingString = line.split(": ");
        // String playerName = incomingString[0];
        // String command = incomingString[1];
        // if (!isExistPlayer(playerName)) {addPlayer(playerName);}
        // if (!isValidAction(command)) {throw new ActionException();}
        // executeCommand(playerName, command);
    }

}
