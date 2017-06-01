package learn.hibernate.security.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.intersection;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Set;

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
    public boolean hasAccess(UserDetails user, MenuCode... securedMenuItems) {
        Set<MenuCode> menu = menuManager.getMenuFor(user);
        return nonNull(menu) && isNotEmpty(intersection(menu, Sets.newHashSet(securedMenuItems)));
    }
}