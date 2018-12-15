package com.nix.jingxun.addp.rpc.common.serializable;

import com.alipay.remoting.CustomSerializer;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.exception.DeserializationException;
import com.alipay.remoting.exception.SerializationException;
import com.alipay.remoting.rpc.RequestCommand;
import com.alipay.remoting.rpc.ResponseCommand;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/08 13:14
 */
@Component("jsonSerializer")
public class JsonSerializer implements CustomSerializer {

    @Override
    public <T extends RequestCommand> boolean serializeHeader(T request, InvokeContext invokeContext) throws SerializationException {
        return false;
    }

    @Override
    public <T extends ResponseCommand> boolean serializeHeader(T response) throws SerializationException {
        return false;
    }

    @Override
    public <T extends RequestCommand> boolean deserializeHeader(T request) throws DeserializationException {
        return false;
    }

    @Override
    public <T extends ResponseCommand> boolean deserializeHeader(T response, InvokeContext invokeContext) throws DeserializationException {
        return false;
    }

    @Override
    public <T extends RequestCommand> boolean serializeContent(T request, InvokeContext invokeContext) throws SerializationException {
        return false;
    }

    @Override
    public <T extends ResponseCommand> boolean serializeContent(T response) throws SerializationException {
        return false;
    }

    @Override
    public <T extends RequestCommand> boolean deserializeContent(T request) throws DeserializationException {
        return false;
    }

    @Override
    public <T extends ResponseCommand> boolean deserializeContent(T response, InvokeContext invokeContext) throws DeserializationException {
        return false;
    }
}
