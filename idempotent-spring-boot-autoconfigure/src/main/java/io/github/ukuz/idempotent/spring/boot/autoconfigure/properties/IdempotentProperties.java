package io.github.ukuz.idempotent.spring.boot.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
@Validated
@ConfigurationProperties("idempotent")
public class IdempotentProperties {

    /**
     * 是否开启幂等
     */
    private boolean enable = true;

    /**
     * 幂等校验失效时间（单位：秒）
     */
    private int expireTime = 60;

    /**
     * 默认采用redis存储
     */
    private String store = "redis";

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
