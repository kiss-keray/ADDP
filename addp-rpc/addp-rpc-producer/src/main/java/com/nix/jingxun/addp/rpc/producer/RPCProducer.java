package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.config.CommonConfig;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.producer.netty.ProducerClient;
import com.nix.jingxun.addp.rpc.producer.netty.ProducerRemotingServer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/12/07 18:53
 */
@Slf4j
public final class RPCProducer {

    private static ProducerRemotingServer nettyServer = ProducerRemotingServer.getServer(CommonConfig.PRODUCER_INVOKE_PORT);

    static {
        nettyServer.start();
    }

    public static Object registerProducer(Class<?> interfaceClass,Object producer, String app, String group, String version,Object newBean) throws Exception {
        // 防止bean被多个factory注入

        if (InvokeContainer.isExistImpl(interfaceClass.getName())) {
            return newBean;
        }
        try {
            String host = (CommonConfig.PRODUCER_INVOKE_LOCALHOST == null ?
                    Inet4Address.getLocalHost().getHostAddress() : CommonConfig.PRODUCER_INVOKE_LOCALHOST) + ":" + CommonConfig.PRODUCER_INVOKE_PORT;
            Producer2ServerRequest request = new Producer2ServerRequest();
            request.setHost(host);
            request.setInterfaceName(interfaceClass.getName());
            request.setAppName(app);
            request.setGroup(group);
            request.setVersion(version);
            Method[] methods = interfaceClass.getMethods();
            if (methods != null && methods.length > 0) {
                request.setMethods(Stream.of(methods).map(item -> new Producer2ServerRequest.MethodMsg(item.getName(), item.getParameterTypes())).toArray(Producer2ServerRequest.MethodMsg[]::new));
            }
            log.info("注册服务 {}",request);
            RPCPackage rpcPackage = RPCPackage.createRequestMessage(RPCPackageCode.PRODUCER_REGISTER);
            rpcPackage.setObject(request);
            RPCPackage response = ProducerClient.CLIENT.invokeSync(CommonConfig.SERVER_HOST, rpcPackage, 10000);
            if (response.getCmdCode() != RPCPackageCode.RESPONSE_SUCCESS) {
                throw new RuntimeException("服务方法注册失败 response:" + response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("服务注册SUCCESS");
        RPCInvoke rpcBean = ASM.changeBean(producer);
        InvokeContainer.addInterface(interfaceClass.getName(), rpcBean);
        return rpcBean;
    }

}