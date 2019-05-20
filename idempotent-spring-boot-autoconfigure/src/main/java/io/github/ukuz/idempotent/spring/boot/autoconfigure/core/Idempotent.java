package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.exception.StoreException;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.properties.IdempotentProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author ukuz90
 * @since 2019-05-16
 */
public final class Idempotent {

    private Store store;
    private IdempotentProperties properties;

    public Idempotent(Store store, IdempotentProperties properties) {
        this.store = store;
        this.properties = properties;
    }

    public boolean canAccess(String uuid) throws StoreException {
        //如果没有Key，就不能调用
        if (StringUtils.isEmpty(uuid)) {
            return false;
        }
        Assert.notNull(this.store, "store must not be null");
        try {
            IdempotentKey idempotentKey = new IdempotentKey();
            idempotentKey.setUuid(uuid);
            idempotentKey.setState(IdempotentKey.STATE_PROCEED);
            if (this.store.contain(idempotentKey)) {
                //如果已经存在Key，则不能通过
                return false;
            }
            if (!this.store.atomicSaveWithExpire(idempotentKey, properties.getExpireTime())) {
                //如果原子保存失败，则不能通过
                return false;
            }

            return true;
        } catch (RuntimeException e) {
            throw new StoreException(e);
        }
    }

    public IdempotentKey getKey(String uuid) throws StoreException {
        if (StringUtils.isEmpty(uuid)) {
            return IdempotentKey.UNKOWN;
        }
        IdempotentKey key = this.store.getKey(uuid);
        //由于store有可能用户实现错了，所以需要校验结果
        key = normalizeIdempotentKey(key);
        return key;
    }

    public boolean saveKey(IdempotentKey key) throws StoreException {
        normalizeIdempotentKey(key);
        return this.store.saveKey(key, properties.getExpireTime());
    }

    private IdempotentKey normalizeIdempotentKey(IdempotentKey key) {
        if (key == null) {
            return IdempotentKey.UNKOWN;
        }

        if (!StringUtils.isEmpty(key.getPayload()) || key.getStatusCode() > 0) {
            key.setState(IdempotentKey.STATE_FINISH);
        } else {
            key.setState(IdempotentKey.STATE_PROCEED);
        }

        return key;
    }


}
