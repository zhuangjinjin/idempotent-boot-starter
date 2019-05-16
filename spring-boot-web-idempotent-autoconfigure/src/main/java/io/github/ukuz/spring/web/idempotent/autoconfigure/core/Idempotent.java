package io.github.ukuz.spring.web.idempotent.autoconfigure.core;

import io.github.ukuz.spring.web.idempotent.autoconfigure.exception.StoreException;
import io.github.ukuz.spring.web.idempotent.autoconfigure.properties.IdempotentProperties;
import org.springframework.util.Assert;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public final class Idempotent {

    private Store store;
    private IdempotentProperties properties;

    public Idempotent(IdempotentProperties properties) {
        this.properties = properties;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean canAccess(Key key) throws StoreException {
        //如果没有Key，就不能调用
        if (key == Key.UNKOWN) {
            return false;
        }
        Assert.notNull(this.store, "store must not be null");
        try {
            if (this.store.contain(key)) {
                //如果已经存在Key，则不能通过
                return false;
            }
            if (!this.store.atomicSaveWithExpire(key, properties.getExpireTime())) {
                //如果原子保存失败，则不能通过
                return false;
            }

            return true;
        } catch (RuntimeException e) {
            throw new StoreException(e);
        }
    }


}
