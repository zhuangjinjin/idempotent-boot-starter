package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.exception.StoreException;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.properties.IdempotentProperties;
import org.springframework.util.Assert;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public final class Idempotent {

    private Store store;
    private IdempotentProperties properties;

    public Idempotent(Store store, IdempotentProperties properties) {
        this.store = store;
        this.properties = properties;
    }

    public boolean canAccess(IdempotentKey idempotentKey) throws StoreException {
        //如果没有Key，就不能调用
        if (idempotentKey == IdempotentKey.UNKOWN) {
            return false;
        }
        Assert.notNull(this.store, "store must not be null");
        try {
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


}
