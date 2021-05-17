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

    public void parseCommand(String incomingComand) throws StagCommandException
    {
        String[] tmp1 = incomingComand.split(": ", 2);
        this.playerName = tmp1[0];
        String[] tmp2 = tmp1[1].split(" "); // tmp==null ???
        this.commands = new ArrayList<String>(Arrays.asList(tmp2));  
    }

    public void processCommand(String playerName, ArrayList<String> commands) throws StagCommandException
    {
        if(!game.isExistPlayer(playerName)) {game.createPlayer(playerName);} // ???
        Player player = game.getPlayer(playerName);
        // check commands.get(0) exists ???
        switch (commands.get(0)) { // case sensitive ???
            case "inv": case "inventory": executeInventory(player, commands); break;
            case "get": executeGet(player, commands); break;
            case "drop": executeDrop(player, commands); break;
            case "goto": executeGoto(player, commands); break;
            case "look": executeLook(player, commands); break;
            case "health": executeHealth(player, commands); break;
            default: executeAction(player, commands);
        }
    }

    public void executeInventory(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 1) {throw new StagCommandException();}
        ArrayList<Artefact> inventory = p.showInventory();
        outputStream.append("You have:\n");
        for (Artefact a : inventory) {outputStream.append("  " + a.getDescription() + "\n");}
    }

    public void executeGet(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException();}
        Location l = p.getLocation();
        Artefact targetArtefact = l.getArtefact(commands.get(1));
        if (targetArtefact==null) {throw new StagCommandException();}
        p.addArtefact(targetArtefact);
        l.removeArtefact(targetArtefact);
        outputStream.append("You picke up " + targetArtefact.getName() + ".");
    }

    public void executeDrop(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException();}
        Location l = p.getLocation();
        Artefact targetArtefact = p.getBelonging(commands.get(1));
        if (targetArtefact==null) {throw new StagCommandException();}
        p.removeArtefact(targetArtefact);
        l.addArtefact(targetArtefact);
        outputStream.append("You drop off " + targetArtefact.getName() + ".");
    }

    public void executeGoto(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 2) {throw new StagCommandException();}
        String destinationName = commands.get(1);
        Location l = p.getLocation();
        if (!l.isValidPath(destinationName)) {throw new StagCommandException();}
        Location destination = game.getLocation(destinationName);
        p.changeLocation(destination);
        lookEnvironment(p);
    }

    public void executeLook(Player p, ArrayList<String> commands) throws StagCommandException
    {
        if (commands.size() != 1) {throw new StagCommandException();}
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
        if (commands.size() != 1) {throw new StagCommandException();}
        outputStream.append("You current health: " + p.getHealth() + "/" + game.getInitialHealthe()); 
    }

    public void executeAction(Player p, ArrayList<String> commands) throws StagCommandException
    {
        String trigger = commands.get(0);
        Action act = game.getAction(trigger);
        if (act==null) {throw new StagCommandException();}
        ArrayList<String> subjectCommands = new ArrayList<String>(commands.subList(1,commands.size()));
        if (!game.isValidSubject(act, subjectCommands)) {throw new StagCommandException();};
        // consumed subjects exist in locatipon or inventory ???
        consumeEntities(p, act);
        produceEntities(p, act);
        outputStream.append(act.getNarration());
        // takeAction(p, consumed, produced);
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
            if (consumed.equals("health")) {outputStream.append(p.decreaseHealth()); continue;}
            throw new StagCommandException();
        }
    }

    public void produceEntities(Player p, Action act) throws StagCommandException
    {
        Location l = p.getLocation();
        Location unplaced = game.getLocation("unplaced"); 
        if (unplaced==null) {throw new StagCommandException();}
        for (String produced : act.getProduced()) { 
            if (unplaced.getCharacter(produced)!=null) {l.addCharacter(unplaced.getCharacter(produced)); continue;}
            if (unplaced.getArtefact(produced)!=null) {p.addArtefact(unplaced.getArtefact(produced)); continue;}
            if (unplaced.getFurniture(produced)!=null) {l.addFurniture(unplaced.getFurniture(produced)); continue;}
            if (game.getLocation(produced)!=null) {p.getLocation().addPath(produced); continue;} 
            if (produced.equals("health")) {p.increaseHealth(); continue;} 
            throw new StagCommandException();  
        }
    }

    public String toString() {return outputStream.toString();}
}
