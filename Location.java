import java.util.*;

public class Location 
{
    private String name;
    private String description;
    private ArrayList<GameCharacter> characters;
    private ArrayList<Artefact> artefacts;
    private ArrayList<Furniture> furnitures;
    private ArrayList<String> paths;

    public Location(String name, String description) 
    {
        this.name = name;
        this.description = description;
        this.characters = new ArrayList<GameCharacter>();
        this.artefacts = new ArrayList<Artefact>();
        this.furnitures = new ArrayList<Furniture>();
        this.paths = new ArrayList<String>();
    }

    public String getName() {return this.name;}
    public String getDescription() {return this.description;}
    public ArrayList<GameCharacter> getCharacters() {return this.characters;}
    public ArrayList<Artefact> getArtefacts() {return this.artefacts;}
    public ArrayList<Furniture> getFurnitures() {return this.furnitures;}
    public ArrayList<String> getPaths() {return this.paths;}

    public void setCharacter(String name, String description)
    {
        if (name==null || description==null) {return;}
        GameCharacter c = new GameCharacter(name, description);
        characters.add(c);
    }

    public void setArtefact(String name, String description)
    {
        if (name==null || description==null) {return;}
        Artefact a = new Artefact(name, description);
        artefacts.add(a);
    }

    public void setFurniture(String name, String description)
    {
        if (name==null || description==null) {return;}
        Furniture f = new Furniture(name, description);
        furnitures.add(f);
    }

    public void setPath(String name){ this.paths.add(name);}

}
