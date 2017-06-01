package learn.hibernate.security.impl;

import static org.apache.commons.collections.CollectionUtils.intersection;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static learn.hibernate.security.MenuCode.BATCH;
import static learn.hibernate.security.MenuCode.EQ;
import static learn.hibernate.security.MenuCode.RF;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import learn.hibernate.security.MenuCode;
import learn.hibernate.security.MenuManager;

@Service
public class BasicMenuManager implements MenuManager {

    private static final Logger LOG = LoggerFactory.getLogger(BasicMenuManager.class);
    private static final MenuCode root = EQ;
    private Map<String, DirectedGraph<MenuCode, DefaultEdge>> customMenus;
    private DirectedGraph<MenuCode, DefaultEdge> menu;

    @PostConstruct
    public void init() {
        initMenu();
        initCustomMenus();
    }

    private void initMenu() {
        this.menu = new DefaultDirectedGraph<>(DefaultEdge.class);
        // add menu items
        menu.addVertex(MenuCode.BATCH);
        menu.addVertex(MenuCode.EQ);
        menu.addVertex(MenuCode.RF);

        // add connections between them
        menu.addEdge(EQ, BATCH);
        menu.addEdge(EQ, RF);
        LOG.info("Main menu: {} ", menu);
    }

    @Override
    public DirectedGraph<MenuCode, DefaultEdge> getMenuFor(UserDetails user) {
        return customMenus.get(user.getUsername());
    }

    private static DirectedGraph<MenuCode, DefaultEdge> getCustomMenu(DirectedGraph<MenuCode, DefaultEdge> menu,
                                                                      Set<MenuCode> granted) {

        Set<MenuCode> allowedMenuItems = new HashSet<>();
        allowedMenuItems.add(root);
        List<MenuCode> submenu = Graphs.successorListOf(menu, root);
        buildCustomMenu(menu, submenu, allowedMenuItems, granted);

        Set<MenuCode> prohibitedMenuItems = new HashSet<>(menu.vertexSet());
        prohibitedMenuItems.removeIf(allowedMenuItems::contains);
        menu.removeAllVertices(prohibitedMenuItems);

        return menu;
    }

    private static void buildCustomMenu(DirectedGraph<MenuCode, DefaultEdge> menu,
                                        List<MenuCode> candidates,
                                        Set<MenuCode> customMenu,
                                        Set<MenuCode> granted) {
        for (MenuCode candidate : candidates) {
            List<MenuCode> neighbors = getNeighborsOf(candidate, candidates);
            List<MenuCode> children = Graphs.successorListOf(menu, candidate);
            boolean anyChildGranted = isAnyGrantedRecursively(menu, children, granted);
            boolean anyNeighborGranted = isAnyGrantedRecursively(menu, neighbors, granted);
            boolean parentGranted = isNotEmpty(intersection(Graphs.predecessorListOf(menu, candidate), customMenu));

            if (granted.contains(candidate) || anyChildGranted || (!anyNeighborGranted && parentGranted)) {
                customMenu.add(candidate);
            }
            buildCustomMenu(menu, children, customMenu, granted);
        }
    }

    private static List<MenuCode> getNeighborsOf(MenuCode candidate, List<MenuCode> candidates) {
        List<MenuCode> neighbors = new ArrayList<>(candidates);
        neighbors.remove(candidate);
        return neighbors;
    }

    private static boolean isAnyGrantedRecursively(DirectedGraph<MenuCode, DefaultEdge> menu,
                                                   List<MenuCode> items,
                                                   Set<MenuCode> granted) {

        boolean anyGranted = false;
        for (MenuCode item : items) {
            anyGranted = granted.contains(item)
                    || isAnyGrantedRecursively(menu, Graphs.successorListOf(menu, item), granted);
        }
        return anyGranted;
    }

    /**
     * The goal of this method is to map user to his custom menu
     */
    private void initCustomMenus() {
        String user1 = "rf";
        ImmutableSet<MenuCode> user1Permissions = ImmutableSet.of(RF);
        LOG.info("User '{}' has the following permissions: {}", user1, user1Permissions);

        String user2 = "batch";
        ImmutableSet<MenuCode> user2Permissions = ImmutableSet.of(BATCH);
        LOG.info("User '{}' has the following permissions: {}", user2, user2Permissions);

        this.customMenus = ImmutableMap.of(
                user1, getCustomMenu(copy(menu), user1Permissions),
                user2, getCustomMenu(copy(menu), user2Permissions));
        LOG.info("Custom menus: {}", customMenus);
    }

    private static DirectedGraph<MenuCode, DefaultEdge> copy(DirectedGraph<MenuCode, DefaultEdge> menu) {
        DirectedGraph<MenuCode, DefaultEdge> menuCopy = new DefaultDirectedGraph<>(DefaultEdge.class);
        Graphs.addGraph(menuCopy, menu);
        return menuCopy;
    }
}
