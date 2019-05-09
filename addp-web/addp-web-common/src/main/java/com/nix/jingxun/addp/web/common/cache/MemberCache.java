package com.nix.jingxun.addp.web.common.cache;

import com.nix.jingxun.addp.web.model.MemberModel;

import javax.servlet.http.HttpSession;

/**
 * @author Kiss
 * @date 2018/05/01 20:08
 * 登录用户缓存
 */
public final class MemberCache {
    public final static String USER_SESSION_KEY = "user_id";
    //本地线程临时存储
    private final static ThreadLocal<HttpSession> local = new ThreadLocal<>();

    //获取当前登录的用户
    public static MemberModel currentUser() {
        return (MemberModel) local.get().getAttribute(USER_SESSION_KEY);
    }
    //将存储了用户的session存到ThreadLocal里
    public static void setCurrentUser(MemberModel memberModel) {
        local.get().setAttribute(USER_SESSION_KEY,memberModel);
    }

    public static void setSession(HttpSession session) {
        local.set(session);
    }
    public static HttpSession getSession() {
        return local.get();
    }
}
