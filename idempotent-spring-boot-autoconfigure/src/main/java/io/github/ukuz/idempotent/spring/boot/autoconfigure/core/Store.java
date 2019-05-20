package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.exception.StoreException;

/**
 * @author ukuz90
 * @since 2019-05-16
 * @see RedisStore
 */
@Spi
public interface Store {

    boolean contain(IdempotentKey idempotentKey) throws StoreException;

    boolean atomicSaveWithExpire(IdempotentKey idempotentKey, int expireTime) throws StoreException;

    IdempotentKey getKey(String uuid) throws StoreException;

    boolean saveKey(IdempotentKey idempotentKey, int expireTime) throws StoreException;

}
