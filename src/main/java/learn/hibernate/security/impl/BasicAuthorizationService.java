package learn.hibernate.security.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.intersection;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import learn.hibernate.security.AuthorizationService;
import learn.hibernate.security.MenuCode;
import learn.hibernate.security.MenuManager;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
@Component
public class BasicAuthorizationService implements AuthorizationService {

    private final MenuManager menuManager;

    @Autowired
    public BasicAuthorizationService(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean hasAccess(UserDetails user, MenuCode... menuItems) {
        DirectedGraph<String, DefaultEdge> menu = menuManager.getMenuFor(user);
        List<String> items = Stream.of(menuItems).map(MenuCode::getValue).collect(Collectors.toList());
        return nonNull(menu) && isNotEmpty(intersection(menu.vertexSet(), items));
    }
}
