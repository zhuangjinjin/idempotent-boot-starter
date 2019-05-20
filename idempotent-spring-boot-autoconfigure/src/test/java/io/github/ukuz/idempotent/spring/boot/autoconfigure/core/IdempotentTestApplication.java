package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.EnableIdempotent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ukuz90
 * @since 2019-05-17
 */
@SpringBootApplication
//@EnableIdempotent
public class IdempotentTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdempotentTestApplication.class, args);
    }

}
