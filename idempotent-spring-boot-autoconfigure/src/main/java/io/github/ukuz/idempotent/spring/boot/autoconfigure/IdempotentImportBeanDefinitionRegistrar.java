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

import io.github.ukuz.idempotent.spring.boot.autoconfigure.core.IdempotentInterceptor;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.core.StoreFactoryBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author ukuz90
 * @since 2019-05-16
 */
public class IdempotentImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableIdempotent.class.getName());
        boolean enableIdempotent = (boolean) annotationAttributes.get("enable");
        if (enableIdempotent) {
            registerWebMvcConfigurer(registry);
            registerStoreFactoryBean(registry);
        }
    }

    private void registerStoreFactoryBean(BeanDefinitionRegistry registry) {
        doRegisterBean(registry, null, StoreFactoryBean.class);
    }

    private void registerWebMvcConfigurer(BeanDefinitionRegistry registry) {
        doRegisterBean(registry, null, IdempotentWebMvcConfigurer.class, (BeanFactory) registry);
    }

    private void doRegisterBean(BeanDefinitionRegistry registry, String beanName, Class<?> clazz, Object...args) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        Stream.of(args).forEach(beanDefinitionBuilder::addConstructorArgValue);

        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        if (StringUtils.isEmpty(beanName)) {
            if (clazz.getSimpleName().length() > 0) {
                beanName = clazz.getSimpleName();
            } else {
                //匿名类
                if (clazz.getInterfaces() != null && clazz.getInterfaces().length > 0) {
                    beanName = clazz.getInterfaces()[0].getSimpleName();
                } else {
                    beanName = clazz.getSuperclass().getSimpleName();
                }
            }
            beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

        }
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    static class IdempotentWebMvcConfigurer implements WebMvcConfigurer {

        private BeanFactory beanFactory;

        public IdempotentWebMvcConfigurer(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new IdempotentInterceptor(beanFactory));
        }
    }
}
