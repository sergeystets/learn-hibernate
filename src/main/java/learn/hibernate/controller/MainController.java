package learn.hibernate.controller;

import static learn.hibernate.security.MenuCode.EQ;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import learn.hibernate.security.annotations.MenuController;

/**
 * @author Sergii Stets
 *         Created: 01.02.2016
 **/
@RestController
@MenuController(EQ)
public class MainController {

    @RequestMapping(value ="/", method = RequestMethod.GET)
    public ModelAndView getMainMenu() {
        return new ModelAndView("eq");
    }
}