package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = PluginLoaderTestApplication.class)
@RunWith(SpringRunner.class)
public class PluginLoaderTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void getPlugin() {
        RedisStore store = (RedisStore) PluginLoader.getLoader(Store.class).getPlugin("redis", applicationContext);
        Assert.assertNotNull("store must RedisStore", store);
        Assert.assertNotNull(store.getApplicationContext());
    }

}