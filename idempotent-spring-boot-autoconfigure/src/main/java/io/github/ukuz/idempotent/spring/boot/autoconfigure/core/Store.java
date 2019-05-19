package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.exception.StoreException;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
@Spi
public interface Store {

    boolean contain(IdempotentKey idempotentKey) throws StoreException;

    boolean atomicSaveWithExpire(IdempotentKey idempotentKey, int expireTime) throws StoreException;

    IdempotentKey getKey(String uuid) throws StoreException;

    boolean saveKey(IdempotentKey idempotentKey, int expireTime) throws StoreException;

}
