import java.util.*;

public class Player 
{
    private String name;
    private int health; //current health
    private int maxHealth; //max health/initial health
    private Location location; //current location
    private Location start; //starting point
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
    { // returns Artefact instance in response to its name
        for (Artefact a : inventory) {if (a.getName().equals(artefactName)) return a;}
        return null;
    }

    public ArrayList<Artefact> showInventory() {return this.inventory;}
    public void addArtefact(Artefact artefact) {inventory.add(artefact);}
    public void removeArtefact(Artefact artefact) {inventory.remove(artefact);}
    public void changeLocation(Location destination) {location = destination;}

    public void increaseHealth() {if(health < maxHealth) health++;}
    public boolean decreaseHealth() 
    {
        if(--health <= 0) {loseAndRestart(); return true;} // health become 0 then lose
        else {return false;}
    }

    public void loseAndRestart()
    { // when a player lose, he/she lose his/her all belonging and goes back to the starting point
        for (Artefact a : inventory) {location.addArtefact(a);};
        this.inventory.clear();
        this.location = this.start;
        this.health = this.maxHealth;
    }
    
}
