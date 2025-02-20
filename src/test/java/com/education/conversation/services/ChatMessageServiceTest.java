package com.education.conversation.services;

import com.education.conversation.dto.enums.ChatModel;
import com.education.conversation.dto.enums.ChatRole;
import com.education.conversation.dto.message.MessageRequestDto;
import com.education.conversation.dto.message.MessageResponseDto;
import com.education.conversation.dto.models.gemini.Content;
import com.education.conversation.dto.models.gemini.GeminiResponse;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.entities.Conversation;
import com.education.conversation.entities.Model;
import com.education.conversation.entities.User;
import com.education.conversation.exceptions.ErrorResponseException;
import com.education.conversation.providers.ProviderProcessorHandler;
import com.education.conversation.repositories.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ChatMessageServiceTest {


    @InjectMocks
    private ChatMessageService chatMessageService;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ConversationService conversationService;
    @Mock
    private ModelService modelService;
    @Mock
    private ProviderProcessorHandler providerProcessorHandler;
    @Mock
    private UserService userService;

    private MessageRequestDto requestDto;
    private MessageResponseDto expectedResponseDto;
    private GeminiResponse geminiResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        when(conversationService.getByIdOrThrowException(requestDto.getConversationId()))
                .thenReturn(new Conversation());

        when(modelService.findModelOrThrowError(requestDto.getModel()))
                .thenReturn(new Model().setId(1L).setModelName(ChatModel.GEMINI_1_5_PRO.getModel()));

        when(conversationService.setNameForConversation(any(), any())).thenReturn(new Conversation());

        when(chatMessageRepository.save(any()))
                .thenReturn(new ChatMessage().setConversation(new Conversation().setId(1L)));

        when(chatMessageRepository.findAllByConversation_Id(any())).thenReturn(new ArrayList<>());
    }

    @Test
    @DisplayName("Исключение при проверке баланса пользователя")
    void handleTextMessage_TestThrowsUserInsufficientFundsException() throws Exception {
        when(userService.findByIdOrThrowError(any()))
                .thenReturn(new User().setBalance(BigDecimal.ZERO));

        assertThrows(ErrorResponseException.class,
                () -> chatMessageService.handleTextMessage(requestDto));


    }

    @Test
    @DisplayName("Исключение при проверке баланса пользователя, в многопоточном методе")
    void handleTextMessageForManyResponses_TestThrowsUserInsufficientFundsException() throws Exception {
        when(userService.findByIdOrThrowError(any()))
                .thenReturn(new User().setBalance(BigDecimal.ZERO));

        assertThrows(ErrorResponseException.class,
                () -> chatMessageService.handleTextMessageForManyResponses(requestDto));


    }

}
