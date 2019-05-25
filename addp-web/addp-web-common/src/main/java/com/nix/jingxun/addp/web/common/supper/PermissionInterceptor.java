package com.nix.jingxun.addp.web.common.supper;

import com.nix.jingxun.addp.web.common.PermissionHandler;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.model.MemberModel;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Kiss
 * @date 2018/05/01 20:07
 * 权限管理
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor, PermissionHandler<Object,Method> {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //将session缓存
        MemberCache.setSession(request.getSession());
        MemberModel memberModel = MemberModel.builder().build();
        memberModel.setId(1L);
        MemberCache.setCurrentUser(memberModel);
//        //获取session缓存用户
//        MemberBaseModel user = (MemberBaseModel) request.getSession().getAttribute(MemberCache.USER_SESSION_KEY);
//        //将session缓存
//        MemberCache.putUser(request.getSession());
//        //对请求方法进行拦截
//        if (handler instanceof HandlerMethod) {
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//            Method method = handlerMethod.getMethod();
//            //判断controller需要执行的方法是否需要权限校验
//            if (methodIsPermission(method)) {
//                Clear methodClear = method.getAnnotation(Clear.class);
//                Clear controllerClear = method.getDeclaringClass().getAnnotation(Clear.class);
//                //如果method没有标识为清除权限校验
//                if (methodClear == null && controllerClear == null) {
//                    boolean ok;
//                    if (user != null) {
//                        //判断用户是否具有方法的执行权限
//                        ok = userPermission(user,method);
//                    } else {
//                        //如果用户未登录
//                        AdminController adminController = method.getDeclaringClass().getAnnotation(AdminController.class);
//                        if (adminController != null) {
//                            response.sendRedirect("/admin/login");
//                        } else {
//                            response.sendRedirect("/member/login");
//                        }
//                        return false;
//                    }
//                    //如果用户权限不足
//                    if (!ok) {
//                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "权限不足");
//                        return ok;
//                    }
//                }
//            }
//        }
        return true;
    }

    /**
     * 判断该方法是否需要拦截权限
     * */
//    private boolean methodIsPermission(Method method) {
//        MemberController memberController = method.getDeclaringClass().getAnnotation(MemberController.class);
//        AdminController adminController = method.getDeclaringClass().getAnnotation(AdminController.class);
//        if (memberController != null || adminController != null) {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean userPermission(MemberBaseModel user, Method method) {
//        if (MemberService.ADMIN_USERNAME.equals(user.getUsername())) {
//            return true;
//        }
//        RoleBaseModel role = user.getRole();
//        List<RoleInterfaceModel> roleInterfaces = role.getRoleInterfaces();
//        for (RoleInterfaceModel roleInterface:roleInterfaces) {
//            if (isHavePermission(roleInterface,method)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isHavePermission(RoleInterfaceModel roleInterface, Method method) {
//        if (!roleInterface.getEnabled()) {
//            return false;
//        }
//        PostMapping postMapping = method.getAnnotation(PostMapping.class);
//        GetMapping getMapping = method.getAnnotation(GetMapping.class);
//        RequestMapping controllerRequestMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class);
//        String[] controllerUrls = controllerRequestMapping != null ? controllerRequestMapping.value() : null;
//        String[] methodUrls = postMapping != null ? postMapping.value() : getMapping.value();
//        for (int i = 0;methodUrls != null && i < methodUrls.length;i ++) {
//            String url = "";
//            if (controllerUrls != null) {
//                for (int j = 0;j < controllerUrls.length; j ++) {
//                    url = controllerUrls[j] + methodUrls[i];
//                    if (url.matches(roleInterface.getUrl().replaceAll("\\*\\*","\\.\\*"))) {
//                        return true;
//                    }
//                }
//            } else {
//                url = methodUrls[i];
//                if (url.matches(roleInterface.getUrl().replaceAll("\\*\\*","\\.\\*"))) {
//                    return true;
//                }
//            }
//            System.out.println(url);
//        }
//        return false;
//    }

    @Override
    public boolean isHavePermission(Object roleInterface, Method method) {
        return false;
    }
}
