import java.util.*;

public class Player 
{
    private String name;
    private int health;
    private int maxHealth;
    private Location location;
    private Location start;
    private ArrayList<Artefact> inventory;
    
    public Player(String name, Location start, int health) 
    {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.location = start;
        this.start = start;
        this.inventory = new ArrayList<Artefact>();
    }

    public String getName() {return this.name;}
    public int getHealth() {return this.health;}
    public Location getLocation() {return this.location;}

    public Artefact getBelonging(String artefactName)
    {
        for (Artefact a : inventory) {if (a.getName().equals(artefactName)) return a;}
        return null;
    }

    public ArrayList<Artefact> showInventory() {return this.inventory;}
    public void addArtefact(Artefact artefact) {inventory.add(artefact);}
    public void removeArtefact(Artefact artefact) {inventory.remove(artefact);}
    public void increaseHealth() {if(health < maxHealth) health++;}

    public String decreaseHealth() 
    {
        if(--health <= 0) {loseAndRestart(); return "You ran out of health and go back to the start point.";}
        else {return "";}
    }

    public void loseAndRestart()
    {
        for (Artefact a : inventory) {location.addArtefact(a);};
        this.inventory.clear();
        this.location = this.start;
        this.health = this.maxHealth;
    }

    public void changeLocation(Location destination) {location = destination;}
    
}
