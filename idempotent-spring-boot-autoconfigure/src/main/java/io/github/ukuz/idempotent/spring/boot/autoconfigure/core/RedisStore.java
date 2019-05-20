package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.exception.StoreException;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.utils.Holder;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author ukuz90
 * @since 2019-05-16
 */
public class RedisStore implements Store {

    private ApplicationContext applicationContext;

    private Holder<ValueOperations> valueOperationsHolder = new Holder<>();

    public RedisStore(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean contain(IdempotentKey idempotentKey) throws StoreException {
        try {
            Object object = opsForValue().get(idempotentKey.getUuid());
            if (object != null) {
                return true;
            }
        } catch (RuntimeException e) {
            doProcessRuntimeException(e);
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean atomicSaveWithExpire(IdempotentKey idempotentKey, int expireTime) throws StoreException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(idempotentKey);
            if (opsForValue().setIfAbsent(idempotentKey.getUuid(), value, expireTime, TimeUnit.SECONDS)) {
                return true;
            }
        } catch (RuntimeException e) {
            doProcessRuntimeException(e);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public IdempotentKey getKey(String uuid) throws StoreException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = (String) opsForValue().get(uuid);
            if (value != null) {
                IdempotentKey key = objectMapper.readValue(value.getBytes(), IdempotentKey.class);
                return key;
            }
        } catch (RuntimeException e) {
            doProcessRuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean saveKey(IdempotentKey idempotentKey, int expireTime) throws StoreException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String value = objectMapper.writeValueAsString(idempotentKey);
            if (opsForValue().setIfPresent(idempotentKey.getUuid(), value, expireTime, TimeUnit.SECONDS)) {
                return true;
            }
        } catch (RuntimeException e) {
            doProcessRuntimeException(e);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected ValueOperations opsForValue() {
        ValueOperations ops = valueOperationsHolder.getVal();
        if (ops == null) {
            synchronized (valueOperationsHolder) {
                ops = valueOperationsHolder.getVal();
                if (ops == null) {
                    valueOperationsHolder.setVal(applyRedisTemplate().opsForValue());
                    ops = valueOperationsHolder.getVal();
                }
            }
        }
        return ops;
    }

    protected RedisTemplate applyRedisTemplate() {
        RedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        return redisTemplate;
    }

    private void doProcessRuntimeException(RuntimeException e) throws StoreException {
        if (e instanceof RedisConnectionFailureException) {
            throw new StoreException(e);
        }
        throw new StoreException(e);
    }
}
