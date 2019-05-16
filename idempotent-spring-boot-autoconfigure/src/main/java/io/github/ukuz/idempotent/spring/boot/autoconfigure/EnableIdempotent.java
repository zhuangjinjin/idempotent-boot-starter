package io.github.ukuz.idempotent.spring.boot.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IdempotentImportSelector.class)
public @interface EnableIdempotent {
}
