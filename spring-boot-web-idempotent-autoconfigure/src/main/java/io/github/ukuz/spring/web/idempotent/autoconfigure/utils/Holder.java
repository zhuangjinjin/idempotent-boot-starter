package io.github.ukuz.spring.web.idempotent.autoconfigure.utils;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public class Holder<T> {

    private volatile T val;

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }
}
