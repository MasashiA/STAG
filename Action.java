import java.util.*;

public class Action
{
    private ArrayList<String> triggers;
    private ArrayList<String> subjects;
    private ArrayList<String> consumed;
    private ArrayList<String> produced;
    private String  narration;

    public Action(ArrayList<String> triggers, ArrayList<String> subjects, ArrayList<String> consumed, ArrayList<String> produced, String narration)
    {
        this.triggers = triggers;
        this.subjects = subjects;
        this.consumed = consumed; 
        this.produced = produced;
        this.narration = narration;
    }

    public ArrayList<String> getTriggers() {return triggers;}
    public ArrayList<String> getSubjects() {return subjects;}
    public ArrayList<String> getConsumed() {return consumed;}
    public ArrayList<String> getProduced() {return produced;}
    public String getNarration() {return narration;}

}