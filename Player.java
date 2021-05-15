import java.util.*;

public class Player 
{
    private String name;
    private int health;
    private String location;
    private ArrayList<String> inventory;
    
    public Player(String name) 
    {
        this.name = name;
        this.health = 3;
        this.location = "start";
        this.inventory = new ArrayList<String>();
    }

    public String getName() {return this.name;}
    public int getHealth() {return this.health;}
    public String getLocation() {return this.location;}
    public ArrayList<String> getInventory() {return this.inventory;}
    public void changeLocation(String location) {this.location = location;} 


    public void commandInventory()
    {

    }

    public void commandGet(String artefact)
    {

    }

    public void commandDrop(String artefact)
    {

    }

    public void commandGoto(String destination)
    {

    }

    public void commandLook()
    {

    }

    // "inventory" (or "inv" for short): lists all of the artefacts currently being carried by the player
    // "get": picks up a specified artefact from current location and puts it into player's inventory
    // "drop": puts down an artefact from player's inventory and places it into the current location
    // "goto": moves from one location to another (if there is a path between the two)
    // "look": describes the entities in the current location and lists the paths to other locations
    
}
