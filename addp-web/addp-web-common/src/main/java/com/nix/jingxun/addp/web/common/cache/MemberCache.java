package com.nix.jingxun.addp.web.common.cache;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.common.util.AESUtil;
import com.nix.jingxun.addp.web.common.util.MD5Util;
import com.nix.jingxun.addp.web.model.MemberModel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiss
 * @date 2018/05/01 20:08
 * 登录用户缓存
 */
@Component
public final class MemberCache {

    @Resource
    private RedisTemplate<String, String> template;

    public final static String USER_SESSION_KEY = "TOKEN";
    //本地线程临时存储
    private final static ThreadLocal<String> local = new ThreadLocal<>();
    private static MemberCache cache;

    //获取当前登录的用户
    public static MemberModel currentUser() {
        return getCache().getMember(local.get());
    }
    //将存储了用户的session存到ThreadLocal里
    public static String setCurrentUser(MemberModel memberModel) {
        if (memberModel == null) {
            throw new NullPointerException("catch member is null");
        }
        String token = memberToken(memberModel);
        getCache().putMember(token,memberModel);
        return token;
    }

    public static void setToken(String token) {
        local.set(token);
    }




    private static String memberToken(MemberModel memberModel) {
        return MD5Util.md5(System.currentTimeMillis() + memberModel.getUsername());
    }


    private MemberModel getMember(String token) {
        return JSON.parseObject(template.opsForValue().get(token),MemberModel.class);
    }


    private void putMember(String token,MemberModel memberModel) {
        if (memberModel == null) {
            return;
        }
        template.opsForValue().set(token, JSON.toJSONString(memberModel),7, TimeUnit.DAYS);
    }

    private static MemberCache getCache() {

        if (cache == null) {
            cache = SpringContextHolder.getBean(MemberCache.class);
        }
        return cache;
    }
}
