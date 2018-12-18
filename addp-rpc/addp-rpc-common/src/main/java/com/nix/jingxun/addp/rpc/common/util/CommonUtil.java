package com.nix.jingxun.addp.rpc.common.util;

import com.nix.jingxun.addp.rpc.common.IClassLoader;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author keray
 * @date 2018/12/15 16:12
 */
public class CommonUtil {

    public static String className2FilePath(String clazzName) {
        return clazzName.replaceAll("\\.","/");
    }
    public static String filepath2ClassName(String filepath) {
        return filepath.replaceAll("/",".");
    }


    public static Class<?> createClassFile(byte[] data,String name) throws Exception {
        String path = CommonUtil.class.getResource("/").getPath() + className2FilePath(name).substring(0,className2FilePath(name).lastIndexOf("/"));
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(CommonUtil.class.getResource("/").getPath() + CommonUtil.className2FilePath(name) + ".class");
        FileOutputStream out = new FileOutputStream(file);
        out.write(data);
        out.close();
        return Class.forName(CommonUtil.filepath2ClassName(name));
    }
    public static Class<?> createClassLoader(byte[] data,String name) throws Exception {
        System.out.println("load class = " + name);
        System.out.println("loader=" + Thread.currentThread().getContextClassLoader().getClass());
        return new IClassLoader(Thread.currentThread().getContextClassLoader()).loadClass(data,name);
    }
}
