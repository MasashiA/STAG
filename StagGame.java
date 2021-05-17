import java.io.*;
import java.util.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class StagGame 
{
    private static ArrayList<Location> locations;
    private static ArrayList<Action> actions;
    private static ArrayList<Player> players;
    private static Location startingPoint;
    private static final int initialHealth = 3;

    public StagGame(String entityFilename, String actionFilename) 
    {
        locations = new ArrayList<Location>();
        actions = new ArrayList<Action>();
        players = new ArrayList<Player>();
        EntityParser.parse(entityFilename, locations); 
        // debugEntity();
        ActionParser.parse(actionFilename, actions);
        // debugAction();
        startingPoint = locations.get(0);
    }

    // public String commandInventory(String playerName, ArrayList<String> commands) throws StagCommandException
    // {
    //     int p_index = getPlayerIndex(playerName);
    //     if (commands.size() != 0) {throw new StagCommandException();}
    //     return players.get(p_index).commandInventory();
    // }

    // public String commandGet(String playerName, ArrayList<String> commands) throws StagCommandException
    // {
    //     Player p = players.get(getPlayerIndex(playerName));
    //     if (commands.size() != 1) {throw new StagCommandException();}
    //     String targetArtefact = commands.get(0);
    //     if (!isExistArtefact(targetArtefact, players.get(p_index).getLocation())) {throw new StagCommandException();}
    //     p.commandGet(targetArtefact);
    //     return "get " + targetArtefact;
    // }



    // public String commandDrop(String playerName, ArrayList<String> commands) throws StagCommandException
    // {
    //     int index = getPlayerIndex(playerName);
    //     if (commands.size() != 0) {throw new StagCommandException();}
    //     return players.get(index).commandDrop();
    // }

    // public String commandGoto(String playerName, ArrayList<String> commands) throws StagCommandException
    // {
    //     int index = getPlayerIndex(playerName);
    //     if (commands.size() != 0) {throw new StagCommandException();}
    //     return players.get(index).commandGoto();
    // }

    // public String commandLook(String playerName, ArrayList<String> commands) throws StagCommandException
    // {
    //     int index = getPlayerIndex(playerName);
    //     if (commands.size() != 0) {throw new StagCommandException();}
    //     return players.get(index).commandLook();
    // }
    
    // public String commandAction(String playerName, ArrayList<String> commands) throws StagCommandException
    // {
        
    // }

    // public int getPlayerIndex(String playerName) 
    // {
    //     for (int i=0; i<players.size(); i++) {
    //         if (players.get(i).getName().equals(playerName)) {return i;}
    //     }
    //     return -1;
    // }

    public void createPlayer(String name) {players.add(new Player(name, startingPoint, initialHealth));} 

    public boolean isExistPlayer(String name)
    {
        for (Player p: players) {if (p.getName().equals(name)) {return true;}}
        return false;
    }

    public Player getPlayer(String name)
    {
        for (Player p : players) {if (p.getName().equals(name)) return p;}
        return null;
    }

    public Location getLocation(String name)
    {
        for (Location l : locations) {if (l.getName().equals(name)) return l;}
        return null;
    }

    public int getInitialHealthe() {return initialHealth;}

    public Action getAction(String name) 
    {
        for (Action a : actions) {if (a.getTriggers().contains(name)) return a;}
        return null;
    }

    public boolean isValidSubject(Action action, ArrayList<String> subjectCommands) 
    {
        for (String subject : action.getSubjects()) {if (!subjectCommands.contains(subject)) return false;}
        return true;
    }

    // public boolean isExistLocation(String name) 
    // {
    //     for (Location l: locations) {if (l.getName().equals(name)) {return true;}}
    //     return false;
    // }

    // public boolean isValidPath(String locationFrom, String locationTo) 
    // {
    //     for (Location l: locations) {
    //         if (l.getName().equals(locationFrom) && l.getPaths().contains(locationTo)) {return true;}
    //     }
    //     return false;
    // }

    //debug
    public static void debugEntity()
    {
        for (int i=0; i<locations.size(); i++) {
            System.out.println(i + ": " + locations.get(i).getName() + " : " + locations.get(i).getDescription());
            for (int j=0; j<locations.get(i).getCharacters().size(); j++) {
                System.out.println("\t" + j + ": " + locations.get(i).getCharacters().get(j).getName() + " : " + locations.get(i).getCharacters().get(j).getDescription()); 
            }
            for (int j=0; j<locations.get(i).getArtefacts().size(); j++) {
                System.out.println("\t" + j + ": " + locations.get(i).getArtefacts().get(j).getName() + " : " + locations.get(i).getArtefacts().get(j).getDescription()); 
            }
            for (int j=0; j<locations.get(i).getFurnitures().size(); j++) {
                System.out.println("\t" + j + ": " + locations.get(i).getFurnitures().get(j).getName() + " : " + locations.get(i).getFurnitures().get(j).getDescription()); 
            }
            for (int j=0; j<locations.get(i).getPaths().size(); j++) {
                System.out.println("\t" + j + ": " + locations.get(i).getPaths().get(j)); 
            }
        }
    }

    //debug
    public static void debugAction()
    {
        for (int i=0; i<actions.size(); i++) {
            System.out.println(i+1 + ":");
            for (int j=0; j<actions.get(i).getTriggers().size(); j++) {
                System.out.print(actions.get(i).getTriggers().get(j) + "/ ");
            }
            System.out.println();
            System.out.println("\t");
            for (int j=0; j<actions.get(i).getSubjects().size(); j++) {
                System.out.print(actions.get(i).getSubjects().get(j) + "/ ");
            }
            System.out.println();
            System.out.println("\t");
            for (int j=0; j<actions.get(i).getConsumed().size(); j++) {
                System.out.print(actions.get(i).getConsumed().get(j) + "/ ");
            }
            System.out.println();
            System.out.println("\t");
            for (int j=0; j<actions.get(i).getProduced().size(); j++) {
                System.out.print(actions.get(i).getProduced().get(j)+ "/ ");
            }
            System.out.println();
            System.out.println("\t");
            System.out.print(actions.get(i).getNarration());
            System.out.println();
        }
    }

}
