package learn.hibernate.security.impl;

import static org.apache.commons.collections.CollectionUtils.intersection;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import learn.hibernate.security.MenuManager;

@Service
public class BasicMenuManager implements MenuManager {

    private static final Logger LOG = LoggerFactory.getLogger(BasicMenuManager.class);
    private static final String root = "EQ";
    private Map<String, DirectedGraph<String, DefaultEdge>> customMenus;
    private DirectedGraph<String, DefaultEdge> menu;

    @PostConstruct
    public void init() {
        initMenu();
        initCustomMenus();
    }

    private void initMenu() {
        this.menu = new DefaultDirectedGraph<>(DefaultEdge.class);
        // add menu items
        menu.addVertex("EQ");
        menu.addVertex("EQ_04");
        menu.addVertex("EA");
        menu.addVertex("EA_50");
        menu.addVertex("BK");
        menu.addVertex("BK_02");
        menu.addVertex("BK_01");

        // add connections between them
        menu.addEdge("EQ", "EQ_04");
        menu.addEdge("EQ_04", "EA");
        menu.addEdge("EA", "EA_50");
        menu.addEdge("EA_50", "BK");
        menu.addEdge("BK", "BK_02");
        menu.addEdge("BK", "BK_01");
        LOG.info("Main menu: {} ", menu);
    }

    @Override
    public DirectedGraph<String, DefaultEdge> getMenuFor(UserDetails user) {
        return customMenus.get(user.getUsername());
    }

    private static DirectedGraph<String, DefaultEdge> getCustomMenu(DirectedGraph<String, DefaultEdge> menu,
                                                                    Set<String> granted) {

        Set<String> allowedMenuItems = new HashSet<>();
        allowedMenuItems.add(root);
        List<String> submenu = Graphs.successorListOf(menu, root);
        buildCustomMenu(menu, submenu, allowedMenuItems, granted);

        Set<String> prohibitedMenuItems = new HashSet<>(menu.vertexSet());
        prohibitedMenuItems.removeIf(allowedMenuItems::contains);
        menu.removeAllVertices(prohibitedMenuItems);

        return menu;
    }

    private static void buildCustomMenu(DirectedGraph<String, DefaultEdge> menu,
                                        List<String> candidates,
                                        Set<String> customMenu,
                                        Set<String> granted) {
        for (String candidate : candidates) {
            List<String> neighbors = getNeighborsOf(candidate, candidates);
            List<String> children = Graphs.successorListOf(menu, candidate);
            List<String> parents = Graphs.predecessorListOf(menu, candidate);

            Supplier<Boolean> grantedExplicitly = () -> granted.contains(candidate);
            Supplier<Boolean> anyChildGranted = () -> isAnyGrantedRecursively(menu, children, granted);
            Supplier<Boolean> anyNeighborGranted = () -> isAnyGrantedRecursively(menu, neighbors, granted);
            Supplier<Boolean> parentGranted = () -> isNotEmpty(intersection(parents, customMenu));

            if (grantedExplicitly.get() || anyChildGranted.get()
                    || (!anyNeighborGranted.get() && parentGranted.get())) {
                customMenu.add(candidate);
            }
            buildCustomMenu(menu, children, customMenu, granted);
        }
    }

    private static List<String> getNeighborsOf(String candidate, List<String> candidates) {
        List<String> neighbors = new ArrayList<>(candidates);
        neighbors.remove(candidate);
        return neighbors;
    }

    private static boolean isAnyGrantedRecursively(DirectedGraph<String, DefaultEdge> menu,
                                                   List<String> items,
                                                   Set<String> granted) {

        boolean anyGranted = false;
        for (String item : items) {
            anyGranted = granted.contains(item)
                    || isAnyGrantedRecursively(menu, Graphs.successorListOf(menu, item), granted);
        }
        return anyGranted;
    }

    /**
     * The goal of this method is to map user to his custom menu
     */
    private void initCustomMenus() {
        String user1 = "admin";
        ImmutableSet<String> user1Permissions = ImmutableSet.of("BK");
        LOG.info("User '{}' has the following permissions: {}", user1, user1Permissions);

        String user2 = "user";
        ImmutableSet<String> user2Permissions = ImmutableSet.of("BK_02");
        LOG.info("User '{}' has the following permissions: {}", user2, user2Permissions);

        this.customMenus = ImmutableMap.of(
                user1, getCustomMenu(copy(menu), user1Permissions),
                user2, getCustomMenu(copy(menu), user2Permissions));
        LOG.info("Custom menus: {}", customMenus);
    }

    private static DirectedGraph<String, DefaultEdge> copy(DirectedGraph<String, DefaultEdge> menu) {
        DirectedGraph<String, DefaultEdge> menuCopy = new DefaultDirectedGraph<>(DefaultEdge.class);
        Graphs.addGraph(menuCopy, menu);
        return menuCopy;
    }
}
