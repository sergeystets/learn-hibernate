package learn.hibernate.controller;

import static learn.hibernate.security.MenuCode.ORDER_PARAMETERS;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import learn.hibernate.security.annotations.MenuController;

@RestController
@MenuController(ORDER_PARAMETERS)
public class WorkWithOrderParameters {

    @RequestMapping(value = "order-parameters", method = RequestMethod.GET)
    public ModelAndView getParameters() {
        return new ModelAndView("order-parameters");
    }
}
