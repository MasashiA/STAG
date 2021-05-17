import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ActionParser 
{
    public static void parse(String actionFilename, ArrayList<Action> actions)
    {

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(actionFilename));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray actionList = (JSONArray) jsonObject.get("actions");
            for (int i=0; i<actionList.size(); i++) {
                JSONObject a = (JSONObject) actionList.get(i);
                ArrayList<String> triggers = parseJSONArray((JSONArray)a.get("triggers"));
                ArrayList<String> subjects = parseJSONArray((JSONArray)a.get("subjects"));
                ArrayList<String> consumed = parseJSONArray((JSONArray)a.get("consumed"));
                ArrayList<String> produced = parseJSONArray((JSONArray)a.get("produced"));
                String narration = (String) a.get("narration");
                Action action = new Action(triggers, subjects, consumed, produced, narration);
                actions.add(action);
            }

        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (ParseException pe) {
            System.out.println(pe);
        } 
        
    }

    public static ArrayList<String> parseJSONArray(JSONArray array) 
    {
        ArrayList<String> list = new ArrayList<String>();
        for (int i=0; i<array.size(); i++) {list.add((String)array.get(i));}
        return list;
    }

}
