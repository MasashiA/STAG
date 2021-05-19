import java.util.*;

public class StagController 
{
    private StagGame game;
    private String playerName;
    private ArrayList<String> commands;
    private StringBuffer outputStream;

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
        this.playerName = tmp[0]; // set the player's name
        this.commands = new ArrayList<String>(Arrays.asList(tmp[1].split(" "))); // set commands as a list
    }

    public void processCommand(String playerName, ArrayList<String> commands) throws StagCommandException
    {
        if(!game.isExistPlayer(playerName)) {game.createPlayer(playerName);} 
        Player player = game.getPlayer(playerName);
        if (commands.get(0).isEmpty()) {throw new StagCommandException("");}
        //if (commands.size() < 1) {throw new StagCommandException();} // if there's no input then return nothing
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
        ArrayList<Artefact> inventory = p.showInventory(); // get a list of inventory from the Player instance
        outputStream.append("You have:\n");
        for (Artefact a : inventory) {outputStream.append("  " + a.getDescription() + "\n");}
    }

    public void executeGet(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException("Usage: get <Artefact>");}
        Location l = p.getLocation(); // get the curret location of the player
        Artefact targetArtefact = l.getArtefact(commands.get(1)); // if there's no such an artefact, null is returned
        if (targetArtefact==null) {throw new StagCommandException("There's no pickable " + commands.get(1) + ".");}
        p.addArtefact(targetArtefact); // add the artefact to the player's inventory
        l.removeArtefact(targetArtefact); // remove the artefact from the location
        outputStream.append("You picke up " + targetArtefact.getName() + ".");
    }

    public void executeDrop(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException("Usage: drop <Artefact>");}
        Location l = p.getLocation(); // get the curret location of the player
        Artefact targetArtefact = p.getBelonging(commands.get(1)); // if the player doesn't have such an artefact, null is returned
        if (targetArtefact==null) {throw new StagCommandException("You don't have a " + commands.get(1) + ".");}
        p.removeArtefact(targetArtefact); // remove the artefact from the player's inventory
        l.addArtefact(targetArtefact); // add the artefact to the location
        outputStream.append("You drop off " + targetArtefact.getName() + ".");
    }

    public void executeGoto(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException("Usage: goto <Location>");}
        String destinationName = commands.get(1); // get the name of destination
        Location l = p.getLocation(); // get the curret location of the player
        if (!l.isValidPath(destinationName)) {throw new StagCommandException("There's no way to " + commands.get(1) + ".");}
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

    // public void executeAction(Player p, ArrayList<String> commands) throws StagCommandException
    // {
    //     String trigger = commands.get(0); // get a trigger word from commands
    //     ArrayList<Action> actions = game.getActions(trigger); // get a list of Action instance that is invoked by the trigger
    //     if (actions.size() < 1) {throw new StagCommandException(commands.get(0) + " is invalid action.");} // if there's no such an action then throw an Exception
    //     ArrayList<String> subjectCommands = new ArrayList<String>(commands.subList(1,commands.size())); // get a list of subject words 
    //     Action act = findAction(actions, subjectCommands); // get a suitable Action instance
    //     checkEntities(p, act); // check if the action is feasible in the situation
    //     outputStream.append(act.getNarration() + "\n"); 
    //     consumeEntities(p, act); // comsume the designated entities
    //     produceEntities(p, act); // produce the designated entities
    // }

    public void executeAction(Player p, ArrayList<String> commands) throws StagCommandException
    {
        ArrayList<Action> actions = findActionsByTrigger(commands);
        Action act = findActionBySubject(actions, commands);
        checkEntities(p, act); // check if the action is feasible in the situation
        outputStream.append(act.getNarration() + "\n"); 
        consumeEntities(p, act); // comsume the designated entities
        produceEntities(p, act); // produce the designated entities
    }

    public ArrayList<Action> findActionsByTrigger(ArrayList<String> commands) throws StagCommandException
    {
        ArrayList<Action> list = new ArrayList<Action>();
        for (Action act : game.getActions()) {
            for (String trigger : act.getTriggers()) {
                if (commands.contains(trigger)) {list.add(act); break;}
            }
        }
        if (list.size() < 1) {throw new StagCommandException("Invalid action (no valid trigger words).");}
        return list;
    } 

    public Action findActionBySubject(ArrayList<Action> actions, ArrayList<String> commands) throws StagCommandException
    { // returns an Action instance that is consinstent with commands. if there's no suitable Action, throws an Exception  
        ArrayList<Action> tmp = new ArrayList<Action>();
        for (Action act : actions) {if(checkSubjectMatch(act, commands)) tmp.add(act);}
        if (tmp.size() < 1) {throw new StagCommandException("Invalid subject commands");}
        if (tmp.size() > 1) {throw new StagCommandException("Commands ambiguous");}
        return tmp.get(0);
    }

    // public Action findAction(ArrayList<Action> actions, ArrayList<String> commands) throws StagCommandException
    // { // returns an Action instance that is consinstent with commands. if there's no suitable Action, throws an Exception  
    //     ArrayList<Action> tmp = new ArrayList<Action>();
    //     for (Action act : actions) {if(isValidSubject(act, commands)) tmp.add(act);}
    //     if (tmp.size() < 1) {throw new StagCommandException("Invalid subject commands");}
    //     if (tmp.size() > 1) {throw new StagCommandException("Commands ambiguous");}
    //     return tmp.get(0);
    // }

    public boolean checkSubjectMatch(Action action, ArrayList<String> commands) 
    { // checks if the set of subject commands is valid to execute the action
        for (String subject : action.getSubjects()) {if (commands.contains(subject)) return true;}
        return false;
    }

    public void checkEntities(Player p, Action act) throws StagCommandException
    {
        Location l = p.getLocation();
        for (String entity : act.getSubjects()) { 
            if (p.getBelonging(entity)==null && l.getCharacter(entity)==null && l.getArtefact(entity)==null && 
                l.getFurniture(entity)==null && !l.isValidPath(entity) && !entity.equals("health")) {
                    throw new StagCommandException("There's no necessary entity: " + entity);
            }
        }
    }

    public void consumeEntities(Player p, Action act) throws StagCommandException
    {
        Location l = p.getLocation();
        for (String consumed : act.getConsumed()) { 
            if (p.getBelonging(consumed)!=null) {p.removeArtefact(p.getBelonging(consumed)); continue;}
            if (l.getCharacter(consumed)!=null) {l.removeCharacter(l.getCharacter(consumed)); continue;}
            if (l.getArtefact(consumed)!=null) {l.removeArtefact(l.getArtefact(consumed)); continue;}
            if (l.getFurniture(consumed)!=null) {l.removeFurniture(l.getFurniture(consumed)); continue;}
            if (l.isValidPath(consumed)) {l.removePath(consumed); continue;} 
            if (consumed.equals("health")) {if(p.decreaseHealth()) outputStream.append("You ran out of health and go back to the start point.\n"); continue;}
            throw new StagCommandException("Something's wrong");
        }
    }

    public void produceEntities(Player p, Action act) throws StagCommandException
    {
        Location l = p.getLocation();
        Location unplaced = game.getLocation("unplaced"); 
        for (String produced : act.getProduced()) { 
            if (unplaced.getCharacter(produced)!=null) {l.addCharacter(unplaced.getCharacter(produced)); continue;}
            if (unplaced.getArtefact(produced)!=null) {p.addArtefact(unplaced.getArtefact(produced)); continue;}
            if (unplaced.getFurniture(produced)!=null) {l.addFurniture(unplaced.getFurniture(produced)); continue;}
            if (game.getLocation(produced)!=null) {p.getLocation().addPath(produced); continue;} 
            if (produced.equals("health")) {p.increaseHealth(); continue;} 
            throw new StagCommandException("Something's wrong");  
        }
    }

    public String toString() {return outputStream.toString();}
}
