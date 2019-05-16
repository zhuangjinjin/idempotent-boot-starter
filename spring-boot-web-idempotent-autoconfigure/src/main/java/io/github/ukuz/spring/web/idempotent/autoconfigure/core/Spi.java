package io.github.ukuz.spring.web.idempotent.autoconfigure.core;

import java.lang.annotation.*;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Spi {

    String value() default "";
}
