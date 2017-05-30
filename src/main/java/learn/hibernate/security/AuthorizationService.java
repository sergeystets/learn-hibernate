package learn.hibernate.security;

import org.springframework.security.core.userdetails.User;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
public interface AuthorizationService {

    boolean authorize(User user, MenuCode menu);

}
