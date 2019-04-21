package com.nix.jingxun.addp.web.start.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author keray
 * @date 2018/12/26 12:36
 */
@Data
@Slf4j
public class Result<T> {
    protected Boolean success;
    protected T data;
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class SuccessResult<T> extends Result<T>{
        public SuccessResult(T data) {
            success = true;
            this.data = data;
        }
    }
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FailResult<T> extends Result<T> {
        private String errorCode;
        private String errorMsg;
        private Exception exception;
        public FailResult() {
            success = false;
        }
    }
    public static <T> FailResult<T> fail(T data,String errorCode,String errorMsg,Exception e) {
        FailResult<T> failResult = new FailResult<>();
        failResult.setData(data);
        failResult.setErrorCode(errorCode);
        failResult.setErrorMsg(errorMsg);
        failResult.setException(e);
        return failResult;
    }
    public static <T> FailResult<T> fail(String errorCode,String errorMsg,Exception e) {
        return fail(null,errorCode,errorMsg,e);
    }
    public static <T> FailResult<T> fail(String errorCode,String errorMsg) {
        return fail(null,errorCode,errorMsg,null);
    }
    public static <T> FailResult<T> fail(String errorCode) {
        return fail(null,errorCode,null,null);
    }
    public static <T> FailResult<T> fail(Exception e) {
        return fail(null,null,null,e);
    }
    public static <T> Result<T> success(T data) {
        return Result.of(() -> data);
    }
    public static <T> Result<T> of(Supplier<T> supplier) {
        try {
            Object result = supplier.get();
            if (result instanceof Result) {
                return (Result<T>) result;
            } else {
                return new SuccessResult<T>((T) result);
            }
        }catch (Exception e) {
            return FailResult.fail(e);
        }
    }
    public <S2> Result<S2> map(Function<T,S2> function) {
        return of(() -> function.apply(this.getData()));
    }

    public Result<T> fetch(Consumer<T> consumer) {
        consumer.accept(getData());
        return this;
    }
    public <S2> Result<S2> flatMap(Function<T,Result<S2>> function) {
        return function.apply(this.getData());
    }

    public <S2> Result<S2> flat(Function<Result<T>,Result<S2>> function) {
        try {
            return function.apply(this);
        }catch (Exception e) {
            return Result.fail(e);
        }
    }
    public Result<T> logFail() {
        if (this instanceof FailResult) {
            log.error("logFail:{}",this.toString());
        }
        return this;
    }
}
