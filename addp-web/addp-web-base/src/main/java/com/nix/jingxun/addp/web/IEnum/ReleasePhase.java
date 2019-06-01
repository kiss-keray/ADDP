package com.nix.jingxun.addp.web.IEnum;

/**
 * @author keray
 * @date 2019/05/20 18:11
 * 发布单发布阶段
 */
public enum  ReleasePhase {
    // 进入正式环境后状态，不可再被使用
    stop,
    // 初始化可以发布阶段
    init,
    // 第一阶段 拉取代码
    pullCode,
    // 第二阶段 构建
    build,
    // 第三阶段 启动中
    start
}
