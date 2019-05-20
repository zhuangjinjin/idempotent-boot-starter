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