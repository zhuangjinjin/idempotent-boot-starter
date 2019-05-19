package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.constants.Constant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(IdempotentResponseBodyController.class)
public class IdempotentResponseBodyAdviceTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void pay() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/idempotent/pay").accept(MediaType.APPLICATION_JSON)
                .header(Constant.X_REQ_SEQ_ID, "1").param("payMoney", "10")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("90"));
    }

}