package com.sparta.areadevelopment.controller;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.areadevelopment.config.SecurityConfig;
import com.sparta.areadevelopment.dto.CommentRequestDto;
import com.sparta.areadevelopment.dto.CommentResponseDto;
import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.filter.MockSpringSecurityFilter;
import com.sparta.areadevelopment.service.CommentService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
        controllers = CommentController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
class CommentControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CommentResponseDto responseDto;
    private LocalDateTime now;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();

        now = LocalDateTime.now();
        responseDto = new CommentResponseDto(
                1L,
                1L,
                "Test Comment",
                0L,
                now,
                now
        );
    }

    // Test Mock User
    private void mockUserSetup() {
        String username = "TestUser1234";
        String nickname = "TestNickname";
        String password = "Abecd1234!";
        String email = "test23@email.com";
        String info = "Test Info";
        User testUser = new User(
                username,
                nickname,
                password,
                email,
                info
        );
        CustomUserDetails testUserDetails = new CustomUserDetails(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "",
                testUserDetails.getAuthorities());
    }

    // Success
    // POST http://localhost:8080/api/boards/{boardId}/comments
    @Test
    @DisplayName("addComment - Success Test")
    void addComment_Success_Test() throws Exception {
        // Given
        this.mockUserSetup();
        CommentRequestDto requestDto = new CommentRequestDto(
                "Test Comment"
        );
        when(commentService.addComment(any(), any(), any())).thenReturn(responseDto);

        // When - Then
        mvc.perform(post("/api/boards/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.boardId").value(1L))
                .andExpect(jsonPath("$.content").value("Test Comment"))
                .andExpect(jsonPath("$.likeCount").value(0L))
                .andExpect(jsonPath("$.createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(commentService).addComment(any(), any(Long.class), any(CommentRequestDto.class));
    }

    // Request Missing
    // POST http://localhost:8080/api/boards/{boardId}/comments
    @MethodSource("request_Missing_Condition")
    @DisplayName("addComment - Request Missing Test")
    @ParameterizedTest(name = "[{index}] condition : {1}")
    void addComment_Request_Missing_Test(String content, String messages)
            throws Exception {
        // Given
        this.mockUserSetup();
        CommentRequestDto requestDto = new CommentRequestDto(content);

        // When - Then
        mvc.perform(post("/api/boards/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    // Success
    // Get http://localhost:8080/api/boards/{boardId}/comments
    @Test
    @DisplayName("getAllComments - Success Test")
    void getAllComments_Success_Test() throws Exception {
        // Given
        List<CommentResponseDto> responseDtoList = List.of(responseDto);
        when(commentService.getAllComments(1L)).thenReturn(responseDtoList);

        // When - Then
        mvc.perform(get("/api/boards/1/comments")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].boardId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Test Comment"))
                .andExpect(jsonPath("$[0].likeCount").value(0L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(commentService).getAllComments(any(Long.class));
    }

    // No Content
    // Get http://localhost:8080/api/boards/{boardId}/comments
    @Test
    @DisplayName("getAllComments - No Content Test")
    void getAllComments_No_Content_Test() throws Exception {
        // Given
        when(commentService.getAllComments(1L)).thenReturn(List.of());

        // When - Then
        mvc.perform(get("/api/boards/1/comments")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(instanceOf(String.class)))
                .andDo(print());

        verify(commentService).getAllComments(any(Long.class));
    }

    // Success
    // Put http://localhost:8080/api/boards/{boardId}/comments/{commentId}
    @Test
    @DisplayName("updateComment - Success Test")
    void updateComment_Success_Test() throws Exception {
        // Given
        this.mockUserSetup();
        CommentRequestDto requestDto = new CommentRequestDto(
                "Updated Comment"
        );
        CommentResponseDto updatedResponseDto = new CommentResponseDto(
                1L,
                1L,
                "Updated Comment",
                0L,
                now,
                now
        );

        when(commentService.updateComment(any(), any(), any())).thenReturn(updatedResponseDto);

        // When - Then
        mvc.perform(put("/api/boards/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.boardId").value(1L))
                .andExpect(jsonPath("$.content").value("Updated Comment"))
                .andExpect(jsonPath("$.likeCount").value(0L))
                .andExpect(jsonPath("$.createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(commentService).updateComment(any(), any(Long.class),
                any(CommentRequestDto.class));
    }

    // Request Missing
    // Put http://localhost:8080/api/boards/{boardId}/comments/{commentId}
    @MethodSource("request_Missing_Condition")
    @DisplayName("updateComment - Request Missing Test")
    @ParameterizedTest(name = "[{index}] condition : {1}")
    void updateComment_Request_Missing_Test(String content, String messages)
            throws Exception {
        // Given
        this.mockUserSetup();
        CommentRequestDto requestDto = new CommentRequestDto(content);

        // When - Then
        mvc.perform(put("/api/boards/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    // Success
    // Delete http://localhost:8080/api/boards/{boardId}/comments/{commentId}
    @Test
    void softDeleteComment() throws Exception {
        // Given
        this.mockUserSetup();
        String answer = "댓글 삭제 성공";
        when(commentService.deleteComment(any(), any())).thenReturn(answer);

        // When - Then
        mvc.perform(delete("/api/boards/1/comments/1")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(instanceOf(String.class)))
                .andDo(print());

        verify(commentService).deleteComment(any(), any(Long.class));
    }

    // Request Missing Condition
    static Stream<Arguments> request_Missing_Condition() {
        return Stream.of(
                arguments(null, "null"),
                arguments("", "blank")
        );
    }
}