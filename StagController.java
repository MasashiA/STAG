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
        String[] tmp1 = incomingComand.split(": ", 2); // get the player's name
        this.playerName = tmp1[0]; // set the player's name
        this.commands = new ArrayList<String>(Arrays.asList(tmp1[1].split(" "))); // set commands as a list
    }

    public void processCommand(String playerName, ArrayList<String> commands) throws StagCommandException
    {
        if(!game.isExistPlayer(playerName)) {game.createPlayer(playerName);} 
        Player player = game.getPlayer(playerName);
        if (commands.size() < 1) {throw new StagCommandException();} // if there's no input then return nothing
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
        ArrayList<Artefact> inventory = p.showInventory();
        outputStream.append("You have:\n");
        for (Artefact a : inventory) {outputStream.append("  " + a.getDescription() + "\n");}
    }

    public void executeGet(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException("Usage: get <Artefact>");}
        Location l = p.getLocation();
        Artefact targetArtefact = l.getArtefact(commands.get(1));
        if (targetArtefact==null) {throw new StagCommandException("There's no pickable " + commands.get(1) + ".");}
        p.addArtefact(targetArtefact);
        l.removeArtefact(targetArtefact);
        outputStream.append("You picke up " + targetArtefact.getName() + ".");
    }

    public void executeDrop(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException("Usage: drop <Artefact>");}
        Location l = p.getLocation();
        Artefact targetArtefact = p.getBelonging(commands.get(1));
        if (targetArtefact==null) {throw new StagCommandException("You don't have a " + commands.get(1) + ".");}
        p.removeArtefact(targetArtefact);
        l.addArtefact(targetArtefact);
        outputStream.append("You drop off " + targetArtefact.getName() + ".");
    }

    public void executeGoto(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException("Usage: goto <Location>");}
        String destinationName = commands.get(1);
        Location l = p.getLocation();
        if (!l.isValidPath(destinationName)) {throw new StagCommandException("There's no way to " + commands.get(1) + ".");}
        Location destination = game.getLocation(destinationName);
        p.changeLocation(destination);
        lookEnvironment(p);
    }

    public void executeLook(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 1) {throw new StagCommandException("The command \"look\" doesn't accept any arguments.");}
        lookEnvironment(p);
    }

    public void lookEnvironment(Player p) throws StagCommandException
    {
        Location l = p.getLocation();
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
        String trigger = commands.get(0);
        ArrayList<Action> actions = game.getActions(trigger);
        if (actions.size() < 1) {throw new StagCommandException(commands.get(0) + " is invalid action.");}
        ArrayList<String> subjectCommands = new ArrayList<String>(commands.subList(1,commands.size()));
        Action act = findAction(actions, subjectCommands);
        checkEntities(p, act);
        outputStream.append(act.getNarration() + "\n");
        consumeEntities(p, act);
        produceEntities(p, act);
    }

    public Action findAction(ArrayList<Action> actions, ArrayList<String> commands) throws StagCommandException
    {
        ArrayList<Action> tmp = new ArrayList<Action>();
        for (Action act : actions) {if(game.isValidSubject(act, commands)) tmp.add(act);}
        if (tmp.size() < 1) {throw new StagCommandException("Invalid subject commands");}
        if (tmp.size() > 1) {throw new StagCommandException("Commands ambiguous");}
        return tmp.get(0);
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
