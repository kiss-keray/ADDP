package com.nix.jingxun.addp.rpc.common.protocol;

import com.alipay.remoting.RemotingAddressParser;
import com.alipay.remoting.Url;
import com.alipay.remoting.config.Configs;
import com.alipay.remoting.rpc.RpcConfigs;
import com.alipay.remoting.util.StringUtils;

import java.lang.ref.SoftReference;
import java.util.Properties;

/**
 * @author Kiss
 * @date 2018/10/20 21:37
 */
public class ARPCAddressParser implements RemotingAddressParser {
    /**
     * @see RemotingAddressParser#parse(String)
     */
    @Override
    public Url parse(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("Illegal format address string [" + url
                    + "], should not be blank! ");
        }
        Url parsedUrl = this.tryGet(url);
        if (null != parsedUrl) {
            return parsedUrl;
        }
        String ip = null;
        String port = null;
        Properties properties = null;

        int size = url.length();
        int pos = 0;
        for (int i = 0; i < size; ++i) {
            if (COLON == url.charAt(i)) {
                ip = url.substring(pos, i);
                pos = i;
                // should not end with COLON
                if (i == size - 1) {
                    throw new IllegalArgumentException("Illegal format address string [" + url
                            + "], should not end with COLON[:]! ");
                }
                break;
            }
            // must have one COLON
            if (i == size - 1) {
                throw new IllegalArgumentException("Illegal format address string [" + url
                        + "], must have one COLON[:]! ");
            }
        }

        for (int i = pos; i < size; ++i) {
            if (QUES == url.charAt(i)) {
                port = url.substring(pos + 1, i);
                pos = i;
                if (i == size - 1) {
                    // should not end with QUES
                    throw new IllegalArgumentException("Illegal format address string [" + url
                            + "], should not end with QUES[?]! ");
                }
                break;
            }
            // end without a QUES
            if (i == size - 1) {
                port = url.substring(pos + 1, i + 1);
                pos = size;
            }
        }

        if (pos < (size - 1)) {
            properties = new Properties();
            while (pos < (size - 1)) {
                String key = null;
                String value = null;
                for (int i = pos; i < size; ++i) {
                    if (EQUAL == url.charAt(i)) {
                        key = url.substring(pos + 1, i);
                        pos = i;
                        if (i == size - 1) {
                            // should not end with EQUAL
                            throw new IllegalArgumentException(
                                    "Illegal format address string [" + url
                                            + "], should not end with EQUAL[=]! ");
                        }
                        break;
                    }
                    if (i == size - 1) {
                        // must have one EQUAL
                        throw new IllegalArgumentException("Illegal format address string [" + url
                                + "], must have one EQUAL[=]! ");
                    }
                }
                for (int i = pos; i < size; ++i) {
                    if (AND == url.charAt(i)) {
                        value = url.substring(pos + 1, i);
                        pos = i;
                        if (i == size - 1) {
                            // should not end with AND
                            throw new IllegalArgumentException("Illegal format address string ["
                                    + url
                                    + "], should not end with AND[&]! ");
                        }
                        break;
                    }
                    // end without more AND
                    if (i == size - 1) {
                        value = url.substring(pos + 1, i + 1);
                        pos = size;
                    }
                }
                properties.put(key, value);
            }
        }
        parsedUrl = new Url(url, ip, Integer.parseInt(port), properties);
        this.initUrlArgs(parsedUrl);
        Url.parsedUrls.put(url, new SoftReference<Url>(parsedUrl));
        return parsedUrl;
    }

    /**
     * @see RemotingAddressParser#parseUniqueKey(String)
     */
    @Override
    public String parseUniqueKey(String url) {
        boolean illegal = false;
        if (StringUtils.isBlank(url)) {
            illegal = true;
        }

        String uniqueKey = StringUtils.EMPTY;
        String addr = url.trim();
        String[] sectors = StringUtils.split(addr, QUES);
        if (!illegal && sectors.length == 2 && StringUtils.isNotBlank(sectors[0])) {
            uniqueKey = sectors[0].trim();
        } else {
            illegal = true;
        }

        if (illegal) {
            throw new IllegalArgumentException("Illegal format address string: " + url);
        }
        return uniqueKey;
    }

    /**
     * @see RemotingAddressParser#parseProperty(String, String)
     */
    @Override
    public String parseProperty(String addr, String propKey) {
        if (addr.contains("?") && !addr.endsWith("?")) {
            String part = addr.split("\\?")[1];
            for (String item : part.split("&")) {
                String[] kv = item.split("=");
                String k = kv[0];
                if (k.equals(propKey)) {
                    return kv[1];
                }
            }
        }
        return null;
    }

    /**
     * @see RemotingAddressParser#initUrlArgs(Url)
     */
    @Override
    public void initUrlArgs(Url url) {
        String connTimeoutStr = url.getProperty(RpcConfigs.CONNECT_TIMEOUT_KEY);
        int connTimeout = Configs.DEFAULT_CONNECT_TIMEOUT;
        if (StringUtils.isNotBlank(connTimeoutStr)) {
            if (StringUtils.isNumeric(connTimeoutStr)) {
                connTimeout = Integer.parseInt(connTimeoutStr);
            } else {
                throw new IllegalArgumentException(
                        "Url args illegal value of key [" + RpcConfigs.CONNECT_TIMEOUT_KEY
                                + "] must be positive integer! The origin url is ["
                                + url.getOriginUrl() + "]");
            }
        }
        url.setConnectTimeout(connTimeout);

        String protocolStr = url.getProperty(RpcConfigs.URL_PROTOCOL);
        byte protocol = ARPCProtocolV1.PROTOCOL_CODE;
        if (StringUtils.isNotBlank(protocolStr)) {
            if (StringUtils.isNumeric(protocolStr)) {
                protocol = Byte.parseByte(protocolStr);
            } else {
                throw new IllegalArgumentException(
                        "Url args illegal value of key [" + RpcConfigs.URL_PROTOCOL
                                + "] must be positive integer! The origin url is ["
                                + url.getOriginUrl() + "]");
            }
        }
        url.setProtocol(protocol);

        String versionStr = url.getProperty(RpcConfigs.URL_VERSION);
        byte version = ARPCProtocolV1.VERSION;
        if (StringUtils.isNotBlank(versionStr)) {
            if (StringUtils.isNumeric(versionStr)) {
                version = Byte.parseByte(versionStr);
            } else {
                throw new IllegalArgumentException(
                        "Url args illegal value of key [" + RpcConfigs.URL_VERSION
                                + "] must be positive integer! The origin url is ["
                                + url.getOriginUrl() + "]");
            }
        }
        url.setVersion(version);

        String connNumStr = url.getProperty(RpcConfigs.CONNECTION_NUM_KEY);
        int connNum = Configs.DEFAULT_CONN_NUM_PER_URL;
        if (StringUtils.isNotBlank(connNumStr)) {
            if (StringUtils.isNumeric(connNumStr)) {
                connNum = Integer.parseInt(connNumStr);
            } else {
                throw new IllegalArgumentException(
                        "Url args illegal value of key [" + RpcConfigs.CONNECTION_NUM_KEY
                                + "] must be positive integer! The origin url is ["
                                + url.getOriginUrl() + "]");
            }
        }
        url.setConnNum(connNum);

        String connWarmupStr = url.getProperty(RpcConfigs.CONNECTION_WARMUP_KEY);
        boolean connWarmup = false;
        if (StringUtils.isNotBlank(connWarmupStr)) {
            connWarmup = Boolean.parseBoolean(connWarmupStr);
        }
        url.setConnWarmup(connWarmup);
    }

    /**
     * try get from cache
     *
     * @param url
     * @return
     */
    private Url tryGet(String url) {
        SoftReference<Url> softRef = Url.parsedUrls.get(url);
        return (null == softRef) ? null : softRef.get();
    }

    private ARPCAddressParser() {

    }

    public static final ARPCAddressParser PARSER = new ARPCAddressParser();
}
