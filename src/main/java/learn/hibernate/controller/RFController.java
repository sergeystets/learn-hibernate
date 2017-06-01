package learn.hibernate.controller;

import static learn.hibernate.security.MenuCode.RF;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import learn.hibernate.security.MenuCode;
import learn.hibernate.security.annotations.SubMenuController;

@RestController
@SubMenuController(parentMenu = RF)
public class RFController {

    @RequestMapping(value = "rf/greeting", method = RequestMethod.GET)
    public String sayHello() {
        return "hello from " + MenuCode.RF + " menu";
    }
}
