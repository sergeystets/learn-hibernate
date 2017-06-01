package learn.hibernate.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import learn.hibernate.security.MenuCode;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SubMenuController {

    MenuCode[] parentMenu();

}
