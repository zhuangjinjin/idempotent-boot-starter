/*
 * Copyright 2019 ukuz90
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
