package learn.hibernate.security;

import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Sergii_Stets on 6/1/2017.
 */
public interface MenuManager {

    Set<MenuCode> getMenuFor(UserDetails user);
}
