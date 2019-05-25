package com.nix.jingxun.addp.web.IEnum;

/**
 * @author keray
 * @date 2019/05/20 17:50
 * addp项目环境值
 */
public enum ADDPEnvironment {
    // 测试环境
    test(44001),
    // 预发环境
    pre(44002),
    // 正式环境
    pro(44003),
    //备份环境（当正式环境只有一台机器时自动生成备份环境，备份环境主机ip无效0.0.0.0 当正式环境机器大于1时自动删除）
    bak(44004);
    private int port;
    ADDPEnvironment(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
