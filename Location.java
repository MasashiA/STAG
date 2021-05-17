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

    public GameCharacter getCharacter(String name)
    { // returns a Character instance in response to its name
        for (GameCharacter gc : characters) {if (gc.getName().equals(name)) return gc;}
        return null;
    }

    public Artefact getArtefact(String name)
    { // returns a Artefact instance in response to its name
        for (Artefact a : artefacts) {if (a.getName().equals(name)) return a;}
        return null;
    }

    public Furniture getFurniture(String name)
    { // returns a Furniture instance in response to its name
        for (Furniture f : furnitures) {if (f.getName().equals(name)) return f;}
        return null;
    }

    public boolean isValidPath(String targetPath)
    { // checks if the input path is valid
        if(this.paths.contains(targetPath)) {return true;}
        return false;
    }

    public void addCharacter(GameCharacter gc) {characters.add(gc);}
    public void addArtefact(Artefact a) {artefacts.add(a);}
    public void addFurniture(Furniture f) {furnitures.add(f);}
    public void addPath(String pathName) {paths.add(pathName);}

    public void removeCharacter(GameCharacter gc) {characters.remove(gc);}
    public void removeArtefact(Artefact a) {artefacts.remove(a);}
    public void removeFurniture(Furniture f) {furnitures.remove(f);}
    public void removePath(String pathName) {paths.remove(pathName);}

    public void setCharacter(String name, String description)
    {
        GameCharacter c = new GameCharacter(name, description);
        characters.add(c);
    }

    public void setArtefact(String name, String description)
    {
        Artefact a = new Artefact(name, description);
        artefacts.add(a);
    }

    public void setFurniture(String name, String description)
    {
        Furniture f = new Furniture(name, description);
        furnitures.add(f);
    }

    public void setPath(String name) {this.paths.add(name);}

}
