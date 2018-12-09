package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.RPCRemotingClient;
import com.nix.jingxun.addp.rpc.common.config.CommonConfig;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.producer.netty.ProducerRemotingServer;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/12/07 18:53
 */
public final class RPCProducer {
    private static final int PORT = Integer.parseInt(System.getProperty("rpc.server.port","15000"));

    private static ProducerRemotingServer nettyServer = ProducerRemotingServer.getServer(PORT);
    static {
        nettyServer.start();
    }
    public static void registerProducer(Object producer,String app,String group,String version) throws RuntimeException{
        Class<?> interfaceClass = producer.getClass().getInterfaces()[0];
//        try {
//            String host = Inet4Address.getLocalHost().getHostAddress() + ":" + ProducerRemotingServer.server.port();
//            Producer2ServerRequest request = new Producer2ServerRequest();
//            request.setHost(host);
//            request.setInterfaceName(interfaceClass.getName());
//            request.setAppName(app);
//            request.setGroup(group);
//            request.setVersion(version);
//            Method[] methods = interfaceClass.getMethods();
//            if (methods != null && methods.length > 0) {
//                request.setMethods(Stream.of(methods).map(item -> new Producer2ServerRequest.MethodMsg(item.getName(),item.getParameterTypes())).toArray(Producer2ServerRequest.MethodMsg[]::new));
//            }
//            RPCPackage rpcPackage = RPCPackage.createRequestMessage(RPCPackageCode.PRODUCER_REGISTER);
//            rpcPackage.setObject(rpcPackage);
//            RPCPackage response =  RPCRemotingClient.CLIENT.invokeSync(CommonConfig.SERVER_URL,rpcPackage,1000);
//            if (response.getCmdCode() != RPCPackageCode.RESPONSE_SUCCESS) {
//                throw new RuntimeException("服务方法注册失败 :" + request);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        InvokeContainer.addInterface(interfaceClass.getName(),producer);
    }
}
