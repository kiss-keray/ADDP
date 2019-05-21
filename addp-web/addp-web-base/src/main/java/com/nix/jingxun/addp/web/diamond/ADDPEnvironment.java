package com.nix.jingxun.addp.web.diamond;

/**
 * @author keray
 * @date 2019/05/20 17:50
 * addp项目环境值
 */
public enum ADDPEnvironment {
    // 测试环境
    TEST(44001),
    // 预发环境
    PRE(44002),
    // 正式环境
    PRO(44003);
    private int port;
    ADDPEnvironment(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
