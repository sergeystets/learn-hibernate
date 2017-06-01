package learn.hibernate.controller;

import static learn.hibernate.security.MenuCode.WORK_WITH_WEEKS_OF_SUPPLY;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import learn.hibernate.security.annotations.MenuController;

@RestController
@MenuController(WORK_WITH_WEEKS_OF_SUPPLY)
public class WeeksOfSupplyController {

    @RequestMapping(value = "weeks-of-supply", method = RequestMethod.GET)
    public ModelAndView getWeeksOfSupply() {
        return new ModelAndView("weeks-of-supply");
    }
}
