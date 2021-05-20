import java.util.*;

public class StagController 
{
    private StagGame game;
    private String playerName;
    private ArrayList<String> commands;
    private StringBuffer outputStream; // temporaty strage of outoput string

    public StagController(StagGame game, String incomingCommand) throws StagCommandException
    {
        this.game = game;
        outputStream = new StringBuffer();
        parseCommand(incomingCommand);
        processCommand(playerName, commands);
    }

    public void parseCommand(String incomingComand)
    {
        String[] tmp = incomingComand.split(": ", 2); // get the player's name
        this.playerName = tmp[0]; // set the player's name to the field
        this.commands = new ArrayList<String>(Arrays.asList(tmp[1].split(" "))); // set commands as a list
    }

    public void processCommand(String playerName, ArrayList<String> commands) throws StagCommandException
    {
        if(!game.isExistPlayer(playerName)) {game.createPlayer(playerName);}  // if the player is new, create a new instance
        Player player = game.getPlayer(playerName); // get the player's instance
        if (commands.get(0).isEmpty()) {throw new StagCommandException("");} // check if there's any commands
        switch (commands.get(0)) { 
            case "inv": case "inventory": executeInventory(player, commands); break;
            case "get": executeGet(player, commands); break;
            case "drop": executeDrop(player, commands); break;
            case "goto": executeGoto(player, commands); break;
            case "look": executeLook(player, commands); break;
            case "health": executeHealth(player, commands); break;
            default: executeAction(player, commands); // process none-default actions
        }
    }

    public void executeInventory(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 1) {throw new StagCommandException("The command \"inventory\" doesn't accept any arguments.");}
        ArrayList<Artefact> inventory = p.getInventory(); // get a list of inventory from the Player instance
        outputStream.append("You have:\n");
        for (Artefact a : inventory) {outputStream.append("  " + a.getDescription() + "\n");}
    }

    public void executeGet(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() < 2) {throw new StagCommandException("Usage: get <Artefact>");}
        Location l = p.getLocation(); // get the curret location of the player
        ArrayList<Artefact> artefacts = l.getArtefacts(); // get the list of artefacts in the location
        Artefact targetArtefact = null; 
        // if the commands include an existing artefact in the location, then get the name (check only the first match) 
        for (Artefact a : artefacts) {if(commands.contains(a.getName())) {targetArtefact = l.getArtefact(a.getName()); break;}}
        if (targetArtefact==null) {throw new StagCommandException("There's no such a pickable artefact.");}
        p.addArtefact(targetArtefact); // add the artefact to the player's inventory
        l.removeArtefact(targetArtefact); // remove the artefact from the location
        outputStream.append("You picke up " + targetArtefact.getName() + ".");
    }

    public void executeDrop(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() < 2) {throw new StagCommandException("Usage: drop <Artefact>");}
        Location l = p.getLocation(); // get the curret location of the player
        ArrayList<Artefact> belongings = p.getInventory(); // get the list of artefacts in the playrer's inventory
        Artefact targetArtefact = null;
        // if the commands include an existing artefact in the inventory then get the name (check only the first match) 
        for (Artefact b : belongings) {if(commands.contains(b.getName())) {targetArtefact = p.getBelonging(b.getName()); break;}}
        if (targetArtefact==null) {throw new StagCommandException("You don't have such an artefact.");}
        p.removeArtefact(targetArtefact); // remove the artefact from the player's inventory
        l.addArtefact(targetArtefact); // add the artefact to the location
        outputStream.append("You drop off " + targetArtefact.getName() + ".");
    }

    public void executeGoto(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() < 2) {throw new StagCommandException("Usage: goto <Location>");}
        Location l = p.getLocation(); // get the curret location of the player
        ArrayList<String> paths = l.getPaths(); // get a list of valid paths (names of the location)
        String destinationName = ""; 
        // if the commands include a name of valid path then get the name (check only the first match) 
        for (String path : paths) {if(commands.contains(path)) {destinationName = path; break;}}
        if (destinationName.isEmpty()) {throw new StagCommandException("There's no way to such a place.");}
        Location destination = game.getLocation(destinationName); // get the Location instance of the destination
        p.changeLocation(destination); // update the player's location to the destination
        lookEnvironment(p); // show the environment
    }

    public void executeLook(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 1) {throw new StagCommandException("The command \"look\" doesn't accept any arguments.");}
        lookEnvironment(p);
    }

    public void lookEnvironment(Player p) throws StagCommandException
    {
        Location l = p.getLocation(); // get the curret location of the player
        outputStream.append("You are in " + l.getDescription() + " " + "You can see:" + "\n");
        for (GameCharacter gc : l.getCharacters()) {outputStream.append("  " + gc.getDescription() + "\n");}
        for (Artefact a : l.getArtefacts()) {outputStream.append("  " + a.getDescription() + "\n");}
        for (Furniture f : l.getFurnitures()) {outputStream.append("  " + f.getDescription() + "\n");}
        outputStream.append("You can access from here:\n");
        for (String str : l.getPaths()) {outputStream.append("  " + str + "\n");}
    }

    public void executeHealth(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 1) {throw new StagCommandException("The command \"health\" doesn't accept any arguments.");}
        outputStream.append("Your current health: " + p.getHealth() + "/" + game.getInitialHealth()); 
    }

    public void executeAction(Player p, ArrayList<String> commands) throws StagCommandException
    {
        ArrayList<Action> actions = findActionsByTrigger(commands); // get all Action instances that match a trigger word in the commands
        Action act = findActionBySubject(actions, commands); // get an Action instance that is consistent with the commands
        checkEntities(p, act); // check if the action is feasible in the situation
        outputStream.append(act.getNarration() + "\n"); 
        consumeEntities(p, act); // comsume the designated entities
        produceEntities(p, act); // produce the designated entities
    }

    public ArrayList<Action> findActionsByTrigger(ArrayList<String> commands) throws StagCommandException
    { // get all Action instances that match a trigger word in the commands. if there's no suitable Action, throws an Exception  
        ArrayList<Action> list = new ArrayList<Action>();
        for (Action act : game.getActions()) {
            for (String trigger : act.getTriggers()) {
                if (commands.contains(trigger)) {list.add(act); break;}
            }
        }
        if (list.size() < 1) {throw new StagCommandException("Invalid action (no valid trigger word).");}
        return list;
    } 

    public Action findActionBySubject(ArrayList<Action> actions, ArrayList<String> commands) throws StagCommandException
    { // returns an Action instance that is consinstent with the commands. if there's less than one or more than two Actions, throws an Exception  
        ArrayList<Action> tmp = new ArrayList<Action>();
        for (Action act : actions) {if(checkFullMatch(act, commands)) tmp.add(act);} 
        if (tmp.size() == 1) {return tmp.get(0);}
        tmp.clear();
        for (Action act : actions) {if(checkPartialMatch(act, commands)) tmp.add(act);}
        if (tmp.size() < 1) {throw new StagCommandException("Invalid subject commands");}
        if (tmp.size() > 1) {throw new StagCommandException("Commands ambiguous or too complicated");}
        return tmp.get(0);
    }

    public boolean checkFullMatch(Action action, ArrayList<String> commands)
    { // check if the commands matches all subjects of the action
        for (String subject : action.getSubjects()) {if (!commands.contains(subject)) return false;}
        return true;
    }

    public boolean checkPartialMatch(Action action, ArrayList<String> commands) 
    { // check if the commands (at least) partially matches the subjects of the action
        for (String subject : action.getSubjects()) {if (commands.contains(subject)) return true;}
        return false;
    }

    public void checkEntities(Player p, Action act) throws StagCommandException
    { // check if the action is feasible in the situation (i.e. all necessary entities exist)
        Location l = p.getLocation();
        for (String entity : act.getSubjects()) { 
            if (p.getBelonging(entity)==null && l.getCharacter(entity)==null && l.getArtefact(entity)==null && 
                l.getFurniture(entity)==null && !l.isValidPath(entity) && !entity.equals("health")) {
                    throw new StagCommandException("There's no necessary entity: " + entity);
            }
        }
    }

    public void consumeEntities(Player p, Action act) throws StagCommandException
    { // comsume the designated entities, process depending on the type of Objects
        Location l = p.getLocation();
        for (String consumed : act.getConsumed()) { 
            if (p.getBelonging(consumed)!=null) {p.removeArtefact(p.getBelonging(consumed));}
            else if (l.getCharacter(consumed)!=null) {l.removeCharacter(l.getCharacter(consumed));}
            else if (l.getArtefact(consumed)!=null) {l.removeArtefact(l.getArtefact(consumed));}
            else if (l.getFurniture(consumed)!=null) {l.removeFurniture(l.getFurniture(consumed));}
            else if (l.isValidPath(consumed)) {l.removePath(consumed);} 
            else if (consumed.equals("health")) {if(p.decreaseHealth()) outputStream.append("You ran out of health and go back to the start point.\n");}
        }
    }

    public void produceEntities(Player p, Action act) throws StagCommandException
    { // produce the designated entities process depending on the type of Objects
        Location l = p.getLocation();
        Location unplaced = game.getLocation("unplaced"); 
        for (String produced : act.getProduced()) { 
            if (unplaced.getCharacter(produced)!=null) {l.addCharacter(unplaced.getCharacter(produced));}
            else if (unplaced.getArtefact(produced)!=null) {p.addArtefact(unplaced.getArtefact(produced));}
            else if (unplaced.getFurniture(produced)!=null) {l.addFurniture(unplaced.getFurniture(produced));}
            else if (game.getLocation(produced)!=null) {p.getLocation().addPath(produced);} 
            else if (produced.equals("health")) {p.increaseHealth();} 
        }
    }

    public String toString() {return outputStream.toString();}
}
