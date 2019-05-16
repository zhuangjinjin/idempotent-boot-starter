package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.exception.StoreException;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
@Spi
public interface Store {

    boolean contain(Key key) throws StoreException;

    boolean atomicSaveWithExpire(Key key, int expireTime) throws StoreException;

}
