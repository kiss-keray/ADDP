package com.nix.jingxun.addp.web.common;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellFunc;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author keray
 * @date 2019/05/16 17:29
 */
@Slf4j
public class ShellExeLog {
    public static final ShellFunc<Object> fail = (error, msg) -> {
        Exception e = (Exception) error;
        log.error(msg, error);
        throw new ShellExeException(msg, e);
    };
    public static final ShellFunc<Object> success = (result, msg) -> {
        if (!ShellUtil.commandIsExec(result.toString())) {
            fail.accept(null, StrUtil.format("{} 命令未找到", msg));
            return;
        }
        log.info("{}shell:{}{}{}", System.lineSeparator(), msg, System.lineSeparator(), result);
    };
}
