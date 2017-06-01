package learn.hibernate;

import static org.apache.commons.collections.CollectionUtils.intersection;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.collect.ImmutableSet;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
public class GraphDemo {

    public static void main(String[] args) {
        DirectedGraph<String, DefaultEdge> menu = new DefaultDirectedGraph<>(DefaultEdge.class);
        // add menu items
        menu.addVertex("EQ");
        menu.addVertex("EQ_1");
        menu.addVertex("EQ_2");
        menu.addVertex("DM");
        menu.addVertex("FD");
        menu.addVertex("DM_1");
        menu.addVertex("DM_2");

        // add connections between them
        menu.addEdge("EQ", "EQ_1");
        menu.addEdge("EQ", "EQ_2");
        menu.addEdge("EQ_1", "DM");
        menu.addEdge("EQ_2", "FD");
        menu.addEdge("DM", "DM_1");
        menu.addEdge("DM", "DM_2");

        DirectedGraph<String, DefaultEdge> custom = buildCustomMenu(menu, ImmutableSet.of("DM"));
        System.out.println(custom);
    }

    private static DirectedGraph<String, DefaultEdge> buildCustomMenu(DirectedGraph<String, DefaultEdge> menu,
                                                                      Set<String> granted) {

        final String parent = "EQ";
        Set<String> availableVertices = new HashSet<>();
        availableVertices.add(parent);
        List<String> children = Graphs.successorListOf(menu, parent);
        constructCustomMenu(menu, children, availableVertices, granted);

        Set<String> nonAllowedVertices = new HashSet<>(menu.vertexSet());
        nonAllowedVertices.removeIf(availableVertices::contains);
        menu.removeAllVertices(nonAllowedVertices);

        return menu;
    }

    private static void constructCustomMenu(DirectedGraph<String, DefaultEdge> menu,
                                            List<String> candidates,
                                            Set<String> availableVertices,
                                            Set<String> granted) {
        for (String candidate : candidates) {
            List<String> neighbors = getNeighborsOf(candidate, candidates);
            boolean anySubItemsGranted = isAnySuccessorGranted(menu, Graphs.successorListOf(menu, candidate), granted);
            boolean neighborsNotGranted = !isAnySuccessorGranted(menu, neighbors, granted);
            boolean parentGranted = isNotEmpty(intersection(Graphs.predecessorListOf(menu, candidate),
                    availableVertices));
            if (granted.contains(candidate) || anySubItemsGranted || (neighborsNotGranted && parentGranted)) {
                availableVertices.add(candidate);
            }
            List<String> children = Graphs.successorListOf(menu, candidate);
            constructCustomMenu(menu, children, availableVertices, granted);
        }
    }

    private static List<String> getNeighborsOf(String candidate, List<String> candidates) {
        List<String> neighbors = new ArrayList<>(candidates);
        neighbors.remove(candidate);
        return neighbors;
    }

    private static boolean isAnySuccessorGranted(DirectedGraph<String, DefaultEdge> menu,
                                                 List<String> children,
                                                 Set<String> granted) {

        boolean anyGranted = false;
        for (String child : children) {
            anyGranted = granted.contains(child)
                    || isAnySuccessorGranted(menu, Graphs.successorListOf(menu, child), granted);
        }
        return anyGranted;
    }
}
