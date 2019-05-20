package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import java.lang.annotation.*;

/**
 * @author ukuz90
 * @since 2019-05-16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Spi {

    String value() default "";
}
