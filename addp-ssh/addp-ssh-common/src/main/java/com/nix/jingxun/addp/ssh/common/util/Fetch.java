package com.nix.jingxun.addp.ssh.common.util;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author keray
 * @date 2019/05/16 17:14
 */
public class Fetch<T> {
    private T data;
    private Exception e;
    private Catch<T> tCatch;

    public Fetch(T data) {
        this.data = data;
    }

    public static <T> Fetch<T> fetch(T data) {
     return new Fetch<>(data);
    }

    public Fetch<T> then(Consumer<T> then) {
        if (e == null) {
            try {
                then.accept(data);
            }catch (Exception e) {
                this.e = e;
                if (tCatch != null) {
                    tCatch.accept(e,data);
                    this.e = null;
                }
            }
        } else {
            if (tCatch != null) {
                tCatch.accept(e,data);
                this.e = null;
            }
        }
        return this;
    }
    public Fetch<T> then1(Function<T,T> then) {
        if (e == null) {
            try {
                data = then.apply(data);
            }catch (Exception e) {
                this.e = e;
                if (tCatch != null) {
                    tCatch.accept(e,data);
                    this.e = null;
                }
            }
        } else {
            if (tCatch != null) {
                tCatch.accept(e,data);
                this.e = null;
            }
        }
        return this;
    }
    public Fetch<T> Catch(Catch<T> tCatch) {
        this.tCatch = tCatch;
        if (e != null) {
            tCatch.accept(e,data);
            this.e = null;
        }
        return this;
    }
    public interface Catch<T> {
        void accept(Exception e, T t);
    }
}
