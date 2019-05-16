package io.github.ukuz.spring.web.idempotent.autoconfigure;

import io.github.ukuz.spring.web.idempotent.autoconfigure.properties.IdempotentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
@Configuration
public class IdempotentAutoConfiguration {

    @Bean
    public IdempotentProperties idempotentProperties() {
        return new IdempotentProperties();
    }

}
