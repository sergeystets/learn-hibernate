package learn.hibernate.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import learn.hibernate.security.MenuCode;

/**
 * @author Sergii Stets
 *         Date 30.05.2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MenuController {

    MenuCode value();
}
