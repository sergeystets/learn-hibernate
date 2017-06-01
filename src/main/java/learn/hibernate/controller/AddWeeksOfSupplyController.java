package learn.hibernate.controller;

import static learn.hibernate.security.MenuCode.WORK_WITH_WEEKS_OF_SUPPLY;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import learn.hibernate.security.annotations.SubMenuController;

@RestController
@SubMenuController(parentMenu = WORK_WITH_WEEKS_OF_SUPPLY)
public class AddWeeksOfSupplyController {

    @RequestMapping(value = "weeks-of-supply/add", method = RequestMethod.GET)
    public ModelAndView addWeeksOfSupply() {
        return new ModelAndView("add-weeks-of-supply");
    }
}
