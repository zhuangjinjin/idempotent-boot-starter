package io.github.ukuz.idempotent.spring.boot.autoconfigure;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.core.Idempotent;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.core.Store;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.properties.IdempotentProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ukuz90
 * @since 2019-05-16
 */
@Configuration
public class IdempotentAutoConfiguration {

    @Bean
    public IdempotentProperties idempotentProperties() {
        return new IdempotentProperties();
    }

    @ConditionalOnBean({IdempotentImportBeanDefinitionRegistrar.IdempotentWebMvcConfigurer.class})
    @Bean
    public Idempotent idempotent(@Autowired Store store, @Autowired IdempotentProperties idempotentProperties) {
        return new Idempotent(store, idempotentProperties);
    }

}
