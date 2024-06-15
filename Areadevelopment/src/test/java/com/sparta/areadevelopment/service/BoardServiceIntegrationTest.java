package com.sparta.areadevelopment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.areadevelopment.dto.BoardRequestDto;
import com.sparta.areadevelopment.dto.BoardResponseDto;
import com.sparta.areadevelopment.entity.Board;
import com.sparta.areadevelopment.entity.Comment;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.repository.BoardRepository;
import com.sparta.areadevelopment.repository.CommentRepository;
import com.sparta.areadevelopment.repository.UserRepository;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

//@TestPropertySource("classpath:application-test.yml")

/**
 * Create - Drop 환경 Test
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class BoardServiceIntegrationTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User testUser1;
    private User testUser2;

    private Board board;
    private BoardRequestDto requestDto;

    @BeforeAll
    void setTestUser1() {
        testUser1 = new User(
                "test11111",
                "TestNickname1",
                "aBcde123!56",
                "test11111@email.com",
                "Test info user1"
        );

        userRepository.save(testUser1);

        requestDto = new BoardRequestDto(
                "Test Title",
                "Test Content"
        );
    }

    void setTestUser2() {
        testUser2 = new User(
                "test22222",
                "TestNickname2",
                "nmjgiS12345!",
                "test22222@email.com",
                "Test info user2"
        );

        userRepository.save(testUser2);
    }

    void createdOneBoardByTestUser1() {
        board = new Board(testUser1, requestDto);
        boardRepository.save(board);
    }

    void createdCommentInBoardByUser(Board board, User user) {
        Comment comment = new Comment(
                "Test comment",
                board,
                user
        );

        commentRepository.save(comment);
    }

    void createdTwentyBoard(User user) {
        for (int i = 0; i < 20; i++) {
            Board board = new Board(user, requestDto);
            boardRepository.save(board);
        }
    }

    void createdAndSoftDeleteBoard(User user) {
        board = new Board(user, requestDto);
        board.softDelete();
        boardRepository.save(board);
    }

    @Test
    @DisplayName("Create Board - Success Test")
    void createBoard_Success_Test() {
        // Given

        // When
        BoardResponseDto responseDto = boardService.createBoard(testUser1, requestDto);

        // Then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo("Test Title");
        assertThat(responseDto.getContent()).isEqualTo("Test Content");
        assertThat(responseDto.getHits()).isEqualTo(0L);
        assertThat(responseDto.getLikeCount()).isEqualTo(0L);
        assertThat(responseDto.getCreatedAt()).isNotNull();
        assertThat(responseDto.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("findBoard - Success Test")
    void findBoard_Success_Test() {
        // Given
        this.createdOneBoardByTestUser1();
        Long boardId = board.getId();

        // When
        BoardResponseDto responseDto = boardService.findBoard(boardId);

        // Then
        assertThat(responseDto.getId()).isEqualTo(board.getId());
        assertThat(responseDto.getTitle()).isEqualTo("Test Title");
        assertThat(responseDto.getContent()).isEqualTo("Test Content");
        assertThat(responseDto.getHits()).isEqualTo(1L);
        assertThat(responseDto.getLikeCount()).isEqualTo(0L);
        assertThat(responseDto.getCreatedAt()).isNotNull();
        assertThat(responseDto.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("updateBoard - Success Test")
    void updateBoard_Success_Test() {
        // Given
        this.createdOneBoardByTestUser1();
        Long boardId = board.getId();
        BoardRequestDto updateRequestDto = new BoardRequestDto(
                "Update Test Title",
                "Update Test Title"
        );

        // When
        BoardResponseDto responseDto = boardService.updateBoard(testUser1, updateRequestDto,
                boardId);

        // Then
        assertThat(responseDto.getId()).isEqualTo(board.getId());
        assertThat(responseDto.getTitle()).isEqualTo("Update Test Title");
        assertThat(responseDto.getContent()).isEqualTo("Update Test Title");
        assertThat(responseDto.getHits()).isEqualTo(0L);
        assertThat(responseDto.getLikeCount()).isEqualTo(0L);
        assertThat(responseDto.getCreatedAt()).isNotNull();
        assertThat(responseDto.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("deleteBoard - Success Test")
    void deleteBoard_Success_Test() {
        // Given
        this.setTestUser2();
        this.createdOneBoardByTestUser1();
        Long boardId = board.getId();

        for (int i = 0; i < 3; i++) {
            this.createdCommentInBoardByUser(board, testUser1);
        }

        for (int i = 0; i < 3; i++) {
            this.createdCommentInBoardByUser(board, testUser2);
        }

        List<Comment> commentList = commentRepository.findByBoardIdAndDeletedAtIsNull(
                boardId);

        // When
        boardService.deleteBoard(testUser1, boardId);

        // Then
        assertThat(board.getDeletedAt()).isNotNull();
        assertThat(commentList.get(0).getDeletedAt()).isNotNull();
        assertThat(commentList.get(1).getDeletedAt()).isNotNull();
        assertThat(commentList.get(2).getDeletedAt()).isNotNull();
        assertThat(commentList.get(3).getDeletedAt()).isNotNull();
        assertThat(commentList.get(4).getDeletedAt()).isNotNull();
        assertThat(commentList.get(5).getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("findAllBoard - Success Test")
    public void findAllBoard_Success_Test() {
        // Given
        this.createdTwentyBoard(testUser1);
        this.createdAndSoftDeleteBoard(testUser1);

        // When
        List<BoardResponseDto> boardList = boardService.findAllBoard();

        // Then
        assertThat(boardList).hasSize(20);
        assertThat(boardList.get(0).getCreatedAt()).isAfter(boardList.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("findAllRecentlyPagination - Success Test")
    void findAllRecentlyPagination_Success_Test() {
        // Given
        int pageOne = 0;
        int pageTwo = 1;
        this.createdTwentyBoard(testUser1);

        // When
        List<BoardResponseDto> boardList1 = boardService.findAllRecentlyPagination(pageOne);
        List<BoardResponseDto> boardList2 = boardService.findAllRecentlyPagination(pageTwo);

        // Then
        assertThat(boardList1).hasSize(10);
        assertThat(boardList1.get(0).getCreatedAt()).isAfter(boardList1.get(1).getCreatedAt());
        assertThat(boardList1).isNotEqualTo(boardList2);
        assertThat(boardList1.get(0)).isNotEqualTo(boardList2.get(0));
        assertThat(boardList1.get(0).getCreatedAt()).isAfter(boardList2.get(0).getCreatedAt());
    }

    @Test
    @DisplayName("findAllLikesPagination - Success Test")
    void findAllLikesPagination_Success_Test() {
        // Given
        int page = 0;
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            Board board = new Board(testUser1, requestDto);
            for (int j = 0; j < random.nextInt(8); j++) {
                boardRepository.incrementLikeCount(board.getId());
            }
            boardRepository.save(board);
        }

        // When
        List<BoardResponseDto> boardList = boardService.findAllLikesPagination(page);

        // Then
        assertThat(boardList).hasSize(10);
        assertThat(boardList.get(0).getLikeCount()).isGreaterThanOrEqualTo(
                boardList.get(1).getLikeCount());
    }

    @Test
    @DisplayName("findAllDatePagination - Success Test")
    void findAllDatePagination_Success_Test() {
        // Given
        int page = 0;
        String startTime = "2024-06-16";
        String endTime = "2025-06-17";
        this.createdTwentyBoard(testUser1);

        // When
        List<BoardResponseDto> boardList = boardService.findAllDatePagination(page, startTime,
                endTime);

        // Then 작성시간 검증방법이 없다...
        assertThat(boardList).hasSize(10);
        assertThat(boardList.get(0).getCreatedAt()).isAfter(boardList.get(1).getCreatedAt());
    }
}