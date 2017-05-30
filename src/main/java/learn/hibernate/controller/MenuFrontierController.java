package learn.hibernate.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RestController;

import learn.hibernate.security.MenuCode;

/**
 * @author Sergii Stets
 *         Date 30.05.2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@RestController
public @interface MenuFrontierController {

    MenuCode menuCode();
}
