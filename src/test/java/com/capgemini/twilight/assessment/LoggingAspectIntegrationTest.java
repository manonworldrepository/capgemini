package com.capgemini.twilight.assessment.aspect;

import com.capgemini.twilight.assessment.author.dto.AuthorRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoggingAspectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthorRequest authorRequest;

    @BeforeEach
    void setUp() {
        authorRequest = new AuthorRequest();
        authorRequest.setName("Test Author");
    }

    @Test
    void testLogAroundAdviceIsApplied(CapturedOutput output) throws Exception {
        mockMvc.perform(post("/author")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authorRequest)))
            .andExpect(status().isCreated());

        String logOutput = output.toString();

        assertThat(logOutput).contains(
            "==> Enter: com.capgemini.twilight.assessment.author.controller.AuthorController.createAuthor()",
            "<== Exit: com.capgemini.twilight.assessment.author.controller.AuthorController.createAuthor()",
            "Execution time ="
        );
    }

    @Test
    void testLogAfterThrowingAdviceIsApplied(CapturedOutput output) throws Exception {
        long nonExistentId = 999L;

        mockMvc.perform(get("/author/{id}", nonExistentId))
            .andExpect(status().isNotFound());

        String logOutput = output.toString();

        assertThat(logOutput).contains(
            "EXCEPTION in com.capgemini.twilight.assessment.author.controller.AuthorController.getAuthorById()",
            "with cause = 'NULL' and exception = 'Author not found with id: 999'",
            "==> Enter: com.capgemini.twilight.assessment.exception.GlobalExceptionHandler.handleResourceNotFoundException()"
        );
    }
}