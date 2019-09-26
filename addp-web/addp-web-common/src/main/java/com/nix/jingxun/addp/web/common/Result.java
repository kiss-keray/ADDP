package com.nix.jingxun.addp.web.common;

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
    public static class SuccessResult<T> extends Result<T> {
        public SuccessResult(T data) {
            success = true;
            this.data = data;
        }
    }

    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FailResult<E extends Exception, T> extends Result<T> {
        private String errorCode;
        private String errorMsg;
        private E exception;

        public FailResult() {
            success = false;
        }
    }

    public static <E extends Exception, T> FailResult<E, T> fail(T data, String errorCode, String errorMsg, E e) {
        FailResult<E, T> failResult = new FailResult<>();
        failResult.setData(data);
        failResult.setErrorCode(errorCode);
        failResult.setErrorMsg(errorMsg);
        failResult.setException(e);
        return failResult;
    }

    public static <E extends Exception, T> FailResult<E, T> fail(String errorCode, String errorMsg, E e) {
        return fail(null, errorCode, errorMsg, e);
    }

    public static <E extends Exception, T> FailResult<E, T> fail(String errorCode, String errorMsg) {
        return fail(null, errorCode, errorMsg, null);
    }

    public static <E extends Exception, T> FailResult<E, T> fail(String errorCode) {
        return fail(null, errorCode, null, null);
    }

    public static <E extends Exception, T> FailResult<E, T> fail(E e) {
        return fail(null, null, null, e);
    }

    public static <T> Result<T> success(T data) {
        return new SuccessResult<>(data);
    }

    public static <T> Result<T> of(Supplier<T> supplier) {
        try {
            T result = supplier.get();
            if (result instanceof Result) {
                return (Result<T>) result;
            } else if (result instanceof Exception) {
                return FailResult.fail((Exception) result);
            } else {
                return SuccessResult.success(result);
            }
        } catch (Exception e) {
            return FailResult.fail(e);
        }
    }

    public <S2> Result<S2> map(Function<T, S2> function) {
        if (this instanceof SuccessResult) {
            return of(() -> function.apply(this.getData()));
        }
        return (Result<S2>) this;
    }

    public Result<T> peek(Consumer<T> consumer) {
        try {
            if (this instanceof SuccessResult) {
                consumer.accept(getData());
            }
        } catch (Exception e) {
            return fail(e);
        }
        return this;
    }

    public <S2> Result<S2> flatMap(Function<T, Result<S2>> function) {
        try {
            return function.apply(this.getData());
        } catch (Exception e) {
            return fail(e);
        }
    }

    public <S2> Result<S2> flat(Function<Result<T>, Result<S2>> function) {
        try {
            return function.apply(this);
        } catch (Exception e) {
            return Result.fail(e);
        }
    }

    public Result<T> failFlat(Function<FailResult<? extends Exception, T>, Result<T>> function) {
        if (this instanceof FailResult) {
            try {
                return function.apply((FailResult<? extends Exception, T>) this);
            } catch (Exception e) {
                return fail(e);
            }
        }
        return this;
    }

    public Result<T> logFail() {
        if (this instanceof FailResult) {
            if (((FailResult) this).getException() != null) {
                ((FailResult) this).getException().printStackTrace();
            }
            log.error("logFail:{}", this.toString());
        }
        return this;
    }

    public Result<T> throwE() {
        if (this instanceof FailResult && ((FailResult) this).getException() != null) {
            throw new RuntimeException(((FailResult) this).getException());
        }
        return this;
    }
}
