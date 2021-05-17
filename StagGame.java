import java.util.*;

public class StagGame 
{
    private static ArrayList<Location> locations; // list of location containing characters, artefacts, and furnitures
    private static ArrayList<Action> actions; // list of actions contining info of subjects, cosumes, produces, and description
    private static ArrayList<Player> players; // list of players
    private static Location startingPoint; // starting location of the game
    private static final int initialHealth = 3; // initial health level (changeable)

    public StagGame(String entityFilename, String actionFilename) 
    {
        locations = new ArrayList<Location>();
        actions = new ArrayList<Action>();
        players = new ArrayList<Player>();
        EntityParser.parse(entityFilename, locations); // read entities from the file  and store them into the list
        ActionParser.parse(actionFilename, actions); // read actions from the file and store them into the list
        startingPoint = locations.get(0); // get the first place as the starting point
    }
    // create a player with the input name, stating point, and initial health level
    public void createPlayer(String name) {players.add(new Player(name, startingPoint, initialHealth));} 

    public boolean isExistPlayer(String name)
    {
        for (Player p: players) {if (p.getName().equals(name)) {return true;}}
        return false;
    }

    public Player getPlayer(String name) 
    { // returns a Player instance in response to its name
        for (Player p : players) {if (p.getName().equals(name)) return p;}
        return null;
    }

    public Location getLocation(String name) 
    { // returns a Location instance in response to its name
        for (Location l : locations) {if (l.getName().equals(name)) return l;}
        return null;
    }

    public int getInitialHealth() {return initialHealth;} // returns the initial health livel (now 3)

    public ArrayList<Action> getActions(String trigger) 
    { // returns a list of Action instances in response to their trigger word
        ArrayList<Action> list = new ArrayList<Action>();
        for (Action a : actions) {if (a.getTriggers().contains(trigger)) list.add(a);}
        return list;
    }

    public boolean isValidSubject(Action action, ArrayList<String> subjectCommands) 
    { // checks if the set of subject commands is valid to execute the action
        for (String subject : action.getSubjects()) {if (!subjectCommands.contains(subject)) return false;}
        return true;
    }

}
