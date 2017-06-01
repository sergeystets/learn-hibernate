package learn.hibernate.controller;

import static learn.hibernate.security.MenuCode.MAIN;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import learn.hibernate.security.annotations.MenuController;

/**
 * @author Sergii Stets
 *         Created: 01.02.2016
 **/
@RestController
@MenuController(MAIN)
public class MainController {

    @RequestMapping(value ="main/greeting", method = RequestMethod.GET)
    public String sayHello() {
        return "hello from " + MAIN + " menu";
    }
}