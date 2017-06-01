package learn.hibernate.security;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Sergii_Stets on 6/1/2017.
 */
public interface MenuManager {

    DirectedGraph<MenuCode, DefaultEdge> getMenuFor(UserDetails user);
}
