package com.nix.jingxun.addp.rpc.producer.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author keray
 * @date 2018/12/07 22:39
 */
public class DynamicProxy implements InvocationHandler {


    @Override
    public Object invoke(Object object, Method method, Object[] args)
            throws Throwable {
        //　　在代理真实对象前我们可以添加一些自己的操作
        System.out.println("before rent house");

        System.out.println("Method:" + method);

        //    当代理对象调用真实对象的方法时，其会自动的跳转到代理对象关联的handler对象的invoke方法来进行调用
//        method.invoke(subject, args);

        //　　在代理真实对象后我们也可以添加一些自己的操作
        System.out.println("after rent house");

        return null;
    }

}