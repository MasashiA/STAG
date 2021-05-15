import java.io.*;
import java.util.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class StagGame 
{
    private static ArrayList<Location> locations;
    private static ArrayList<Action> actions;
    private static ArrayList<Player> players;

    public StagGame(String entityFilename, String actionFilename) 
    {
        locations = new ArrayList<Location>();
        actions = new ArrayList<Action>();
        players = new ArrayList<Player>();
        EntityParser.parse(entityFilename, locations); 
        // debugEntity();
        ActionParser.parse(actionFilename, actions);
        //debugAction();
    }

    public void createPlayer(String name)
    {
        players.add(new Player(name));
    } 

    public boolean isExistPlayer(String name)
    {
        for (Player p: players) {if (p.getName().equals(name)) {return true;}}
        return false;
    }

    public boolean isExistLocation(String name) 
    {
        for (Location l: locations) {if (l.getName().equals(name)) {return true;}}
        return false;
    }

    public boolean isValidPath(String locationFrom, String locationTo) 
    {
        for (Location l: locations) {
            if (l.getName().equals(locationFrom) && l.getPaths().contains(locationTo)) {return true;}
        }
        return false;
    }

    public boolean isExistAction(String trigger, String subjectString) 
    {
        for (Action a: actions) {
            if (a.getTriggers().contains(trigger)) {
                for (int i=0; i<a.getSubjects().size(); i++) {
                    String subject = (String) a.getSubjects().get(i); // ClassCastException ??
                    if(!subjectString.contains(subject)) {return false;}
                }
            }
            return true;
        }
        return false;
    }

    public Action getAction(String trigger, String subjectString) 
    {
        isExistAction(trigger, subjectString);
        
    }

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
