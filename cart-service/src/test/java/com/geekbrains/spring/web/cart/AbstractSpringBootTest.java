package com.geekbrains.spring.web.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import javax.transaction.Transactional;

@SpringBootTest
//@Transactional
@AutoConfigureMockMvc
public class AbstractSpringBootTest {
    @Autowired
    private MockMvc mockMvc;

    public ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }
}
