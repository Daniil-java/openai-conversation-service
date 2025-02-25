package com.education.conversation.controllers;

import com.education.conversation.dto.enums.ChatModel;
import com.education.conversation.dto.enums.ChatRole;
import com.education.conversation.dto.message.MessageRequestDto;
import com.education.conversation.dto.message.MessageResponseDto;
import com.education.conversation.dto.models.gemini.Content;
import com.education.conversation.dto.models.gemini.GeminiResponse;
import com.education.conversation.integrations.GeminiFeignClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "GEMINI_TOKEN=GEMINI_TOKEN",
        "GENERATION_TOKEN=GENERATION_TOKEN"
})
public class ChatMessageControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3");
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    private GeminiFeignClient geminiFeignClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private MessageRequestDto requestDto;
    private MessageResponseDto expectedResponseDto;
    private GeminiResponse geminiResponse;

    @AfterAll
    void tearDown() {
        postgreSQLContainer.stop();
    }

    @BeforeAll
    void setUpDatabase() {
        postgreSQLContainer.start();
        jdbcTemplate.update("INSERT INTO users (id, name) VALUES (?, ?)", 1L, "test");
        jdbcTemplate.update("INSERT INTO conversations (id, user_id) VALUES (?, ?)", 1L, 1L);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("UPDATE users SET balance = ? WHERE id = ?", BigDecimal.ONE, 1L);

        requestDto = new MessageRequestDto()
                .setContent("Test")
                .setModel(ChatModel.GEMINI_1_5_PRO)
                .setConversationId(1L);

        expectedResponseDto = new MessageResponseDto()
                .setContent("Test")
                .setRole(ChatRole.ASSISTANT)
                .setInputToken(BigDecimal.valueOf(1.2500))
                .setOutputToken(BigDecimal.valueOf(5.0000));

        geminiResponse = new GeminiResponse()
                .setUsageMetadata(
                        new GeminiResponse.UsageMetadata()
                                .setCandidatesTokenCount(1)
                                .setPromptTokenCount(1)
                                .setTotalTokenCount(2)
                )
                .setCandidates(List.of(
                        new GeminiResponse.Candidate()
                                .setContent(new Content()
                                        .setRole(ChatRole.ASSISTANT.toString())
                                        .setParts(
                                                List.of(new Content.Part().setText("Test"))
                                        ))));

    }

    private BigDecimal getBalance(Long userId) {
        String getQuery = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(
                getQuery, new Object[]{userId}, (rs, rowNum) -> rs.getBigDecimal("balance")
        );
    }


    @Test
    void sendMessage_ReturnMessageResponseDto() throws Exception {
        assertEquals(getBalance(1L), BigDecimal.ONE, "Ошибка начальных параметров");

        doReturn(geminiResponse).when(geminiFeignClient).generate(any(), any(), any());

        this.mockMvc.perform(post("/api/v1/messages/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(expectedResponseDto))
                );

        assertEquals(getBalance(1L).compareTo(BigDecimal.ZERO), -1);
    }

    @Test
    void sendMessageForManyResponses_ReturnListMessageResponseDto() throws Exception {
        assertEquals(getBalance(1L), BigDecimal.ONE, "Ошибка начальных параметров");

        doReturn(geminiResponse).when(geminiFeignClient).generate(any(), any(), any());

        this.mockMvc.perform(post("/api/v1/messages/concurrent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.size()").value(5)
                );

        assertEquals(getBalance(1L).compareTo(BigDecimal.ZERO), -1);
    }

}
