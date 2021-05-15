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
                JSONArray triggers = (JSONArray) a.get("triggers");
                JSONArray subjects = (JSONArray) a.get("subjects");
                JSONArray consumed = (JSONArray) a.get("consumed");
                JSONArray produced = (JSONArray) a.get("produced");
                String narration = (String) a.get("narration");
                Action action = new Action(triggers, subjects, consumed, produced, narration);
                actions.add(action);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } 
        
    }

}
