package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ukuz90
 * @since 2019-05-16
 */
public final class IdempotentKey implements Serializable {

    public static final IdempotentKey UNKOWN = new IdempotentKey();

    public static final int STATE_PROCEED = 1;
    public static final int STATE_FINISH = 2;

    private String uuid;
    private int state;
    private int statusCode;
    private String statusMsg;
    private String payload;
    private Map<String, String> header;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
