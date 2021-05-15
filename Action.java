import org.json.simple.JSONArray;

public class Action
{
    private JSONArray triggers;
    private JSONArray subjects;
    private JSONArray consumed;
    private JSONArray produced;
    private String  narration;

    public Action(JSONArray triggers, JSONArray subjects, JSONArray consumed, JSONArray produced, String narration)
    {
        this.triggers = triggers;
        this.subjects = subjects;
        this.consumed = consumed; 
        this.produced = produced;
        this.narration = narration;
    }

    public JSONArray getTriggers() {return triggers;}
    public JSONArray getSubjects() {return subjects;}
    public JSONArray getConsumed() {return consumed;}
    public JSONArray getProduced() {return produced;}
    public String getNarration() {return narration;}

}