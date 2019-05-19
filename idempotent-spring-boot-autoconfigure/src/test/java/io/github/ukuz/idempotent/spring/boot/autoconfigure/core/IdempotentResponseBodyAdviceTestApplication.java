package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.EnableIdempotent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ukuz90
 * @date 2019-05-19
 */
@SpringBootApplication
@EnableIdempotent
public class IdempotentResponseBodyAdviceTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdempotentResponseBodyAdviceTestApplication.class, args);
    }

}
