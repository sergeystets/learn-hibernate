package learn.hibernate.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
public interface AuthorizationService {

    boolean hasAccess(UserDetails user, MenuCode... securedMenuItems);

}
