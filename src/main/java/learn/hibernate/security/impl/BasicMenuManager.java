package learn.hibernate.security.impl;

import static learn.hibernate.security.MenuCode.EQ;
import static learn.hibernate.security.MenuCode.RF;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import learn.hibernate.security.MenuCode;
import learn.hibernate.security.MenuManager;

@Service
public class BasicMenuManager implements MenuManager {

    private Map<String, Set<MenuCode>> customMenus;

    @PostConstruct
    public void init() {
        initCustomMenus();
    }


    @Override
    public Set<MenuCode> getMenuFor(UserDetails user) {
        return customMenus.get(user.getUsername());
    }

    /**
     * The goal of this method is to map user to his custom menu
     */
    private void initCustomMenus() {
        // map user to it's custom menu
        this.customMenus = ImmutableMap.of("sergeystets", ImmutableSet.of(EQ, RF));
    }
}
