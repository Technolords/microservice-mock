package net.technolords.util.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        TodoAPI.class,
})
@ActiveProfiles("todo")
class TodoAPITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoAPITest.class);
    private static MockMvc mockMvc;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void initializeWebContext(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    @DisplayName("Happy: get todo")
    void testTodo() throws Exception {
        // Setup
        LOGGER.info("About to test...");
        // Request
        MvcResult mvcResult = mockMvc.perform(get(
                TodoAPI.END_POINT)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // Verify
        Assertions.assertEquals(MediaType.APPLICATION_JSON_UTF8_VALUE, mvcResult.getResponse().getContentType());
    }
}