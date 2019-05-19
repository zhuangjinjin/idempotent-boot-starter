package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.exception.StoreException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = IdempotentTestApplication.class)
@RunWith(SpringRunner.class)
public class IdempotentTest {

    @Autowired
    private Idempotent idempotent;

    @Test
    public void canAccess() throws StoreException {
        boolean ret = idempotent.canAccess("");
        Assert.assertFalse(ret);
    }
}