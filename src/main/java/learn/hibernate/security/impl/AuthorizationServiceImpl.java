package learn.hibernate.security.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import learn.hibernate.security.AuthorizationService;
import learn.hibernate.security.MenuCode;

/**
 * @author Sergii Stets
 *         Date 31.05.2017
 */
@Component
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public boolean authorize(User user, MenuCode menu) {
        return true;
    }
}
