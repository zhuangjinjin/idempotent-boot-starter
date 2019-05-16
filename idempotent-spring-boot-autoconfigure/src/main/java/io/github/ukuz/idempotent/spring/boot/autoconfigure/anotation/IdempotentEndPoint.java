package io.github.ukuz.idempotent.spring.boot.autoconfigure.anotation;

import java.lang.annotation.*;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdempotentEndPoint {

}
