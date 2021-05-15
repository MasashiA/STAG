import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class EntityParser {

    public static void parse(String filename, ArrayList<Location> locations) {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(filename);
            parser.parse(reader);
            ArrayList<Graph> graphs = parser.getGraphs();
            ArrayList<Graph> subGraphs = graphs.get(0).getSubgraphs();
            ArrayList<String> locNames = new ArrayList<String>(); // not nec?
            for(Graph g : subGraphs){
                ArrayList<Graph> subGraphs1 = g.getSubgraphs();
                for (Graph g1 : subGraphs1){
                    ArrayList<Node> nodesLoc = g1.getNodes(false);
                    Node nLoc = nodesLoc.get(0);
                    Location loc = new Location(nLoc.getId().getId(), nLoc.getAttribute("description")); 
                    locNames.add(nLoc.getId().getId()); // not nec?
                    // System.out.println(loc.getName() + " : " + loc.getDescription()); // debug
                    ArrayList<Graph> subGraphs2 = g1.getSubgraphs();
                    for (Graph g2 : subGraphs2) {
                        ArrayList<Node> nodesEnt = g2.getNodes(false);
                        for (Node nEnt : nodesEnt) {
                            switch (g2.getId().getId()) {
                                case "characters": loc.setCharacter(nEnt.getId().getId(), nEnt.getAttribute("description")); break;
                                case "artefacts": loc.setArtefact(nEnt.getId().getId(), nEnt.getAttribute("description")); break;
                                case "furniture": loc.setFurniture(nEnt.getId().getId(), nEnt.getAttribute("description")); break;
                                //default: throw new Exception();
                            }
                        }
                    }
                    locations.add(loc);
                }

                ArrayList<Edge> edges = g.getEdges();
                for (Edge e : edges){
                    String from = e.getSource().getNode().getId().getId();
                    String to = e.getTarget().getNode().getId().getId();
                    for (Location l : locations) {
                        if (!locNames.contains(from) || !locNames.contains(to)) {System.out.println("ERROR");} // not nec??
                        if (l.getName().equals(from)) {l.setPath(to);}
                    }
                }
            }
            
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (com.alexmerz.graphviz.ParseException pe) {
            System.out.println(pe);
        }
    }
}
