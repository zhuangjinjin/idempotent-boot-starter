package io.github.ukuz.spring.web.idempotent.autoconfigure.core;

import io.github.ukuz.spring.web.idempotent.autoconfigure.properties.IdempotentProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public class StoreFactoryBean implements FactoryBean<Store>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Store getObject() {
        IdempotentProperties properties = applicationContext.getBean(IdempotentProperties.class);
        return (Store) PluginLoader.getLoader(Store.class).getPlugin(properties.getStore(), applicationContext);
    }

    @Override
    public Class<?> getObjectType() {
        return Store.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
