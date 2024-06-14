package com.sparta.areadevelopment.controller;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import com.sparta.areadevelopment.dto.BoardRequestDto;
import com.sparta.areadevelopment.dto.BoardResponseDto;
import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.filter.MockSpringSecurityFilter;
import com.sparta.areadevelopment.service.BoardService;
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

/*
  테스트 자체는 아주 좋은 테스트이지만 실무에서는 이렇게까지 빡빡하게 검증하는것은 나의 할일을 증가시킴
  나중에 요구사항이나 코드 변경이 생기면 '유지보수 2배 이벤트' 발생
  지금은 공부를 위해서 최대한 빡빡한 테스트 코드를 작성하자
  다만 적어도 벨리데이션 체크만큼은 꼼꼼히 하는게 맞는것 같음
 */
@WebMvcTest(
        controllers = BoardController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
class BoardControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    private BoardResponseDto responseDto;
    private LocalDateTime now;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();

        now = LocalDateTime.now();
        responseDto = new BoardResponseDto(
                1L,
                "Test Title",
                "Test Content",
                0L,
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
    // POST http://localhost:8080/api/boards
    @Test
    @DisplayName("createBoard - Success Test")
    void createBoard_Success_Test() throws Exception {
        // Given
        this.mockUserSetup();
        BoardRequestDto requestDto = new BoardRequestDto(
                "Test Title",
                "Test Content"
        );
        when(boardService.createBoard(any(), any())).thenReturn(responseDto);

        // When - Then
        mvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.hits").value(0L))
                .andExpect(jsonPath("$.count").value(0L))
                .andExpect(jsonPath("$.createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(boardService).createBoard(any(), any(BoardRequestDto.class));
    }

    // Request Missing
    // POST http://localhost:8080/api/boards
    @MethodSource("request_Missing_Condition")
    @DisplayName("createBoard - Request Missing Test")
    @ParameterizedTest(name = "[{index}] condition : {2}")
    void createBoard_Request_Missing_Test(String title, String content, String messages)
            throws Exception {
        // Given
        this.mockUserSetup();
        BoardRequestDto requestDto = new BoardRequestDto(title, content);

        // When - Then
        mvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    // Success
    // GET http://localhost:8080/api/boards
    @Test
    @DisplayName("findAllBoard - Success Test")
    void findAllBoard_Success_Test() throws Exception {
        // Given
        List<BoardResponseDto> responseDtoList = List.of(responseDto);
        when(boardService.findAllBoard()).thenReturn(responseDtoList);

        // When - Then
        mvc.perform(get("/api/boards")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].hits").value(0L))
                .andExpect(jsonPath("$[0].count").value(0L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(boardService).findAllBoard();
    }

    // No Content
    // GET http://localhost:8080/api/boards
    @Test
    @DisplayName("findAllBoard - No Content Test")
    void findAllBoard_No_Content_Test() throws Exception {
        // Given
        when(boardService.findAllBoard()).thenReturn(List.of());

        // When - Then
        mvc.perform(get("/api/boards")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(instanceOf(String.class)))
                .andDo(print());

        verify(boardService).findAllBoard();
    }

    // Success
    // Get http://localhost:8080/api/boards/recently/{page}
    @Test
    @DisplayName("findAllRecentlyPagination - Success Test")
    void findAllRecentlyPagination_Success_Test() throws Exception {
        // Given
        List<BoardResponseDto> responseDtoList = List.of(responseDto);
        when(boardService.findAllRecentlyPagination(0)).thenReturn(responseDtoList);

        // When - Then
        mvc.perform(get("/api/boards/recently/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].hits").value(0L))
                .andExpect(jsonPath("$[0].count").value(0L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(boardService).findAllRecentlyPagination(0);
    }

    // No Content
    // Get http://localhost:8080/api/boards/recently/{page}
    @Test
    @DisplayName("findAllRecentlyPagination - No Content Test")
    void findAllRecentlyPagination_No_Content_Test() throws Exception {
        // Given
        when(boardService.findAllRecentlyPagination(0)).thenReturn(List.of());

        // When - Then
        mvc.perform(get("/api/boards/recently/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(instanceOf(String.class)))
                .andDo(print());

        verify(boardService).findAllRecentlyPagination(0);
    }

    // Success
    // Get http://localhost:8080/api/boards/like/{page}
    @Test
    @DisplayName("findAllLikesPagination - Success Test")
    void findAllLikesPagination_Success_Test() throws Exception {
        // Given
        List<BoardResponseDto> responseDtoList = List.of(responseDto);
        when(boardService.findAllLikesPagination(0)).thenReturn(responseDtoList);

        // When - Then
        mvc.perform(get("/api/boards/like/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].hits").value(0L))
                .andExpect(jsonPath("$[0].count").value(0L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(boardService).findAllLikesPagination(0);
    }

    // No Content
    // Get http://localhost:8080/api/boards/like/{page}
    @Test
    @DisplayName("findAllLikesPagination - No Content Test")
    void findAllLikesPagination_No_Content_Test() throws Exception {
        // Given
        when(boardService.findAllLikesPagination(0)).thenReturn(List.of());

        // When - Then
        mvc.perform(get("/api/boards/like/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(instanceOf(String.class)))
                .andDo(print());

        verify(boardService).findAllLikesPagination(0);
    }

    // Success
    // Get http://localhost:8080/api/boards/date/{page}
    @Test
    @DisplayName("findAllDatePagination - Success Test")
    void findAllDatePagination_Success_Test() throws Exception {
        // Given
        List<BoardResponseDto> responseDtoList = List.of(responseDto);
        String startTime = "2024-06-12";
        String endTime = "2024-06-13";
        when(boardService.findAllDatePagination(0, startTime, endTime)).thenReturn(responseDtoList);

        // When - Then
        mvc.perform(get("/api/boards/date/1")
                        .param("startTime", startTime)
                        .param("endTime", endTime)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].hits").value(0L))
                .andExpect(jsonPath("$[0].count").value(0L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(boardService).findAllDatePagination(0, startTime, endTime);
    }

    // No Content
    // Get http://localhost:8080/api/boards/date/{page}
    @Test
    @DisplayName("findAllDatePagination - No Content")
    void findAllDatePagination_No_Content_Test() throws Exception {
        // Given
        String startTime = "2024-06-12";
        String endTime = "2024-06-13";
        when(boardService.findAllDatePagination(0, startTime, endTime)).thenReturn(List.of());

        // When - Then
        mvc.perform(get("/api/boards/date/1")
                        .param("startTime", startTime)
                        .param("endTime", endTime)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(instanceOf(String.class)))
                .andDo(print());

        verify(boardService).findAllDatePagination(0, startTime, endTime);
    }

    // Success
    // Put http://localhost:8080/api/boards/{boardId}
    @Test
    @DisplayName("updateBoard - Success Test")
    void updateBoard_Success_Test() throws Exception {
        // Given
        this.mockUserSetup();
        BoardRequestDto requestDto = new BoardRequestDto(
                "Updated Title",
                "Updated Content"
        );

        BoardResponseDto updatedResponseDto = new BoardResponseDto(
                1L,
                "Updated Title",
                "Updated Content",
                0L,
                0L,
                now,
                now
        );

        when(boardService.updateBoard(any(), any(), any())).thenReturn(updatedResponseDto);

        // When - Then
        mvc.perform(put("/api/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.hits").value(0L))
                .andExpect(jsonPath("$.count").value(0L))
                .andExpect(jsonPath("$.createdAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.modifiedAt")
                        .value(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(print());

        verify(boardService).updateBoard(any(), any(BoardRequestDto.class), any(Long.class));
    }

    // Request Missing
    // Put http://localhost:8080/api/boards/{boardId}
    @MethodSource("request_Missing_Condition")
    @DisplayName("updateBoard - Request Missing Test")
    @ParameterizedTest(name = "[{index}] condition : {2}")
    void updateBoard_Request_Missing_Test(String title, String content, String messages)
            throws Exception {
        // Given
        this.mockUserSetup();
        BoardRequestDto requestDto = new BoardRequestDto(title, content);

        // When - Then
        mvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    // Success
    // Delete http://localhost:8080/api/boards/{boardId}
    @Test
    @DisplayName("deleteBoard - Success Test")
    void deleteBoard_Success_Test() throws Exception {
        // Given
        this.mockUserSetup();
        doNothing().when(boardService).deleteBoard(any(), any());

        // When - Then
        mvc.perform(delete("/api/boards/1")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(instanceOf(String.class)))
                .andDo(print());

        verify(boardService).deleteBoard(any(), any(Long.class));
    }

    // Request Missing Condition
    static Stream<Arguments> request_Missing_Condition() {
        return Stream.of(
                arguments(null, null, "null, null"),
                arguments(null, "", "null, blank"),
                arguments(null, "Content", "null, \"Content\""),
                arguments("", "", "blank, blank"),
                arguments("", null, "blank, null"),
                arguments("", "Content", "blank, \"Content\""),
                arguments("Title", null, "\"Title\", null"),
                arguments("Title", "", "\"Title\", blank")
        );
    }
}