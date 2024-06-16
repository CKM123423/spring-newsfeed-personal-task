package com.sparta.areadevelopment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.areadevelopment.dto.BoardRequestDto;
import com.sparta.areadevelopment.dto.CommentRequestDto;
import com.sparta.areadevelopment.dto.CommentResponseDto;
import com.sparta.areadevelopment.entity.Board;
import com.sparta.areadevelopment.entity.Comment;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.repository.BoardRepository;
import com.sparta.areadevelopment.repository.CommentRepository;
import com.sparta.areadevelopment.repository.UserRepository;
import java.util.List;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    private Board board;

    private Comment comment;

    private CommentRequestDto requestDto;

    @BeforeAll
    void setTestUser1AndBoard() {
        testUser1 = new User(
                "test11111",
                "TestNickname1",
                "aBcde123!56",
                "test11111@email.com",
                "Test info user1"
        );

        userRepository.save(testUser1);

        BoardRequestDto boardRequestDto = new BoardRequestDto(
                "Test Title",
                "Test Content"
        );

        board = new Board(
                testUser1,
                boardRequestDto
        );

        boardRepository.save(board);
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

    void createdComment(User user, Board board) {
        comment = new Comment(
                "Test Comment",
                board,
                user
        );

        commentRepository.save(comment);
    }

    void createBoardAndSoftDelete(User user) {
        BoardRequestDto boardRequestDto = new BoardRequestDto(
                "Test Title",
                "Test Content"
        );
        board = new Board(user, boardRequestDto);
        board.softDelete();
        boardRepository.save(board);
    }

    void createCommentAndSoftDelete(User user, Board board) {
        Comment deleteComment = new Comment(
                "Test Comment",
                board,
                user
        );
        deleteComment.delete();
        commentRepository.save(deleteComment);
    }

    @Test
    @DisplayName("addComment - Success Test")
    void addComment_Success_Test() {
        // Given
        requestDto = new CommentRequestDto("Test Comment");
        Long boardId = board.getId();

        // When
        CommentResponseDto responseDto = commentService.addComment(testUser1, boardId, requestDto);

        // Then
        assertThat(responseDto.getBoardId()).isEqualTo(boardId);
        assertThat(responseDto.getContent()).isEqualTo("Test Comment");
        assertThat(responseDto.getLikeCount()).isEqualTo(0L);
        assertThat(responseDto.getCreatedAt()).isNotNull();
        assertThat(responseDto.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("getAllComments - Success Test")
    void getAllComments_Success_Test() {
        // Given
        Long boardId = board.getId();

        this.setTestUser2();

        for (int i = 0; i < 3; i++) {
            createdComment(testUser1, board);
        }

        for (int i = 0; i < 3; i++) {
            createdComment(testUser2, board);
        }

        this.createCommentAndSoftDelete(testUser1, board);

        // When
        List<CommentResponseDto> commentList = commentService.getAllComments(boardId);

        // Then
        assertThat(commentList).hasSize(6);
        assertThat(commentList.get(0).getCreatedAt()).isAfter(commentList.get(1).getCreatedAt());
        assertThat(commentList.get(0).getBoardId()).isEqualTo(boardId);
        assertThat(commentList.get(0).getContent()).isEqualTo("Test Comment");
        assertThat(commentList.get(0).getLikeCount()).isEqualTo(0L);
        assertThat(commentList.get(0).getCreatedAt()).isNotNull();
        assertThat(commentList.get(0).getModifiedAt()).isNotNull();

    }

    @Test
    @DisplayName("updateComment - Success Test")
    void updateComment_Success_Test() {
        // Given
        Long boardId = board.getId();
        this.createdComment(testUser1, board);

        CommentRequestDto updateRequestDto = new CommentRequestDto("Update Comment");

        // When
        CommentResponseDto responseDto = commentService.updateComment(testUser1.getId(),
                comment.getId(), updateRequestDto);

        // Then
        assertThat(responseDto.getId()).isEqualTo(comment.getId());
        assertThat(responseDto.getBoardId()).isEqualTo(boardId);
        assertThat(responseDto.getContent()).isEqualTo("Update Comment");
        assertThat(responseDto.getLikeCount()).isEqualTo(0L);
        assertThat(responseDto.getCreatedAt()).isNotNull();
        assertThat(responseDto.getModifiedAt()).isNotNull();

    }

    @Test
    @DisplayName("deleteComment - Success Test")
    void deleteComment_Success_Test() {
        // Given
        this.createdComment(testUser1, board);
        Long userId = testUser1.getId();
        Long commentId = comment.getId();

        // When
        String result = commentService.deleteComment(userId, commentId);

        // Then
        assertThat(result).isEqualTo("댓글 삭제 성공");
        assertThat(comment.getDeletedAt()).isNotNull();
    }
}