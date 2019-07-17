package com.nix.jingxun.addp.rpc.common.util;

import com.nix.jingxun.addp.rpc.common.IClassLoader;
import com.nix.jingxun.addp.rpc.common.RPCContext;
import com.nix.jingxun.addp.rpc.common.protocol.RPCRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * @author keray
 * @date 2018/12/15 16:12
 */
@Slf4j
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
        log.info("load class {} ", name);
        return new IClassLoader(Thread.currentThread().getContextClassLoader()).loadClass(data,name);
    }
    public static RPCRequest createInvokeRPCRequest(String proxyInterface, String method, Object[] args) {
        RPCRequest request = new RPCRequest();
        request.setContext(RPCContext.getContext());
        request.setInterfaceName(proxyInterface);
        request.setMethod(method);
        request.setDate(new Date());
        if (args != null && args.length > 0) {
            RPCRequest.ParamsData[] paramsData = new RPCRequest.ParamsData[args.length];
            for (int i = 0;i < args.length;i ++) {
                if (args[i] == null) {
                    paramsData[i] = new RPCRequest.ParamsData(null,null);
                } else {
                    paramsData[i] = new RPCRequest.ParamsData(args[i].getClass().getName(),args[i]);
                }
            }
            request.setParamData(paramsData);
        }
        return request;
    }

}
