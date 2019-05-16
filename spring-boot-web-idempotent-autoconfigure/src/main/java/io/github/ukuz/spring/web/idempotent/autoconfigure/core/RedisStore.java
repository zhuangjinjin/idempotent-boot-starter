package io.github.ukuz.spring.web.idempotent.autoconfigure.core;

import io.github.ukuz.spring.web.idempotent.autoconfigure.exception.StoreException;
import org.springframework.context.ApplicationContext;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public class RedisStore implements Store {

    private ApplicationContext applicationContext;

    public RedisStore(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean contain(Key key) throws StoreException {
        return false;
    }

    @Override
    public boolean atomicSaveWithExpire(Key key, int expireTime) throws StoreException {
        return false;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
