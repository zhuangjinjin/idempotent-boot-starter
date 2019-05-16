package io.github.ukuz.spring.web.idempotent.autoconfigure.core;

import io.github.ukuz.spring.web.idempotent.autoconfigure.exception.StoreException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public class RedisStore implements Store, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    public boolean contain(Key key) throws StoreException {

        return false;
    }

    @Override
    public boolean atomicSaveWithExpire(Key key, int expireTime) throws StoreException {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.redisConnectionFactory = applicationContext.getBean(RedisConnectionFactory.class);
    }
}
