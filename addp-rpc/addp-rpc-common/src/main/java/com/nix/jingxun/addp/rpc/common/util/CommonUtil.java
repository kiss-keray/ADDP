package com.nix.jingxun.addp.rpc.common.util;

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
}
