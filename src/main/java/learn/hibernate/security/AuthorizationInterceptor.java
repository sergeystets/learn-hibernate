package learn.hibernate.security;

import static java.util.Objects.nonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import learn.hibernate.security.annotations.MenuController;
import learn.hibernate.security.annotations.SubMenuController;

/**
 * @author Sergii Stets
 *         Date 30.05.2017
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private static final String ACCESS_DENIED = "Access denied";

    private final AuthorizationService authorizationService;

    @Autowired
    public AuthorizationInterceptor(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        if (supports(handler)) {
            MenuCode[] securedMenuItems = getSecuredMenuItems((HandlerMethod) handler);
            if (nonNull(securedMenuItems)) {
                boolean assessDenied = !authorizationService.hasAccess(currentUser(), securedMenuItems);
                if (assessDenied) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, ACCESS_DENIED);
                }
            }
        }
        return true;
    }

    private MenuCode[] getSecuredMenuItems(HandlerMethod handler) {
        MenuController menu = handler.getBeanType().getAnnotation(MenuController.class);
        SubMenuController subMenu = handler.getBeanType().getAnnotation(SubMenuController.class);

        if (nonNull(menu)) {
            return new MenuCode[]{menu.value()};
        } else if (nonNull(subMenu)) {
            return subMenu.parentMenu();
        }
        return null;
    }

    private UserDetails currentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }
}
