package learn.hibernate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import learn.hibernate.entity.User;
import learn.hibernate.security.MenuCode;
import learn.hibernate.services.IUserService;

/**
 * @author Sergii Stets
 *         Created: 01.02.2016
 **/
@MenuFrontierController(menuCode = MenuCode.USER)
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = {"/user"}, method = RequestMethod.GET)
    public Iterable<User> getUsers() {
        LOG.info("Requesting all users");
        return userService.listUsers();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User getById(@PathVariable("id") Long id) {
        LOG.info("Requesting user by id " + id);
        return userService.findUserById(id).orElse(null);
    }
}