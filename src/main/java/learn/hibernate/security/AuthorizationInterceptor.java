package learn.hibernate.security;

import static java.util.Objects.nonNull;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import learn.hibernate.controller.MenuFrontierController;

/**
 * @author Sergii Stets
 *         Date 30.05.2017
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private static final String ACCESS_DENIED = "Access denied";

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            MenuFrontierController menuInfo = method.getBeanType().getAnnotation(MenuFrontierController.class);
            if (nonNull(menuInfo)) {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                boolean isAuthorized = authorizationService.authorize(user, menuInfo.menuCode());
                if (!isAuthorized) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, ACCESS_DENIED);
                }
            } else {
                return true;
            }
        }
        return true;
    }
}
