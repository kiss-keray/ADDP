package com.nix.jingxun.addp.rpc.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author keray
 * @date 2018/12/16 18:09
 */

public class IClassLoader extends ClassLoader {
    private final static Map<String,Class> LOADER_CLASS = new HashMap<>(32);
    private byte[] data;

    public IClassLoader( ClassLoader parent) {
        super(parent);
    }

    /**
     * 继承ClassLoader进行重写,加载目标类的字节码
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.endsWith("BeanInfo")) {
            name = name.substring(0,name.lastIndexOf("BeanInfo"));
        }
        if (name.endsWith("Customizer")) {
            name = name.substring(0,name.lastIndexOf("Customizer"));
        }
        if (LOADER_CLASS.containsKey(name)) {
            return LOADER_CLASS.get(name);
        }
        byte[] classData = data;
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        Class clazz = defineClass(name, classData, 0, classData.length);
        LOADER_CLASS.put(name,clazz);
        return clazz;
    }

    public synchronized Class<?> loadClass(byte[] data,String name) throws ClassNotFoundException {
        this.data = data;
        return super.loadClass(name);
    }
}

