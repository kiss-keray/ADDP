package com.nix.jingxun.addp.web.start.controller;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.web.exception.WebRunException;

/**
 * @author keray
 * @date 2019/05/13 18:43
 */
public class BaseController {
    <E extends Exception,T> Result<T> failFlat(Result.FailResult<E, T> fail) {
        if (fail.getException() instanceof WebRunException) {
            WebRunException e = (WebRunException) fail.getException();
            fail.setErrorCode(StrUtil.isBlank(fail.getErrorCode()) ? e.getCode().name() : fail.getErrorCode());
            fail.setErrorMsg(StrUtil.isBlank(fail.getErrorMsg()) ? e.getMessage() : fail.getErrorMsg());
        }
        return fail;
    }

}
