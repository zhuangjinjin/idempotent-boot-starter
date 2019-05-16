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

    private IdempotentProperties properties;

    @Override
    public Store getObject() {
        return (Store) PluginLoader.getLoader(Store.class).getPlugin(properties.getStore());
    }

    @Override
    public Class<?> getObjectType() {
        return Store.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.properties = applicationContext.getBean(IdempotentProperties.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
