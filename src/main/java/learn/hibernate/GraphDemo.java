package learn.hibernate;

import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
public class GraphDemo {

    public static void main(String[] args) {
        DirectedGraph<String, DefaultEdge> menu = new DefaultDirectedGraph<>(DefaultEdge.class);

        // add menu items
        menu.addVertex("EQ");
        menu.addVertex("DM");
        menu.addVertex("FK");

        // add connections between them
        menu.addEdge("EQ", "DM");
        menu.addEdge("EQ", "FK");
        menu.addEdge("EQ", "EQ");

        System.out.println(menu);

        // get parent menu items
        List<String> parentMenuItems = Graphs.predecessorListOf(menu, "FK");
        System.out.println(parentMenuItems);
    }
}
