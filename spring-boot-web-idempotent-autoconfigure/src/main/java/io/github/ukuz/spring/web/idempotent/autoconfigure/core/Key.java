package io.github.ukuz.spring.web.idempotent.autoconfigure.core;

import java.io.Serializable;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public final class Key implements Serializable {

    public static final Key UNKOWN = new Key();

    public static final int STATE_START = 1;
    public static final int STATE_FINISH = 2;

    private String key;
    private int state;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
