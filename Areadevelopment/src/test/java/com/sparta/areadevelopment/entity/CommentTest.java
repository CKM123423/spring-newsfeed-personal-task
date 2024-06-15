package com.sparta.areadevelopment.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.sparta.areadevelopment.dto.BoardRequestDto;
import com.sparta.areadevelopment.dto.CommentRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentTest {

    private User user;
    private Board board;
    private BoardRequestDto boardRequestDto;
    private CommentRequestDto commentRequestDto;
    private Comment comment;

    @BeforeEach
    @DisplayName("Comment 생성")
    void setUp() {
        user = Mockito.mock(User.class);

        boardRequestDto = new BoardRequestDto();
        boardRequestDto.setTitle("Test Board Title");
        boardRequestDto.setContent("Test Board Content");

        board = new Board(
                user,
                boardRequestDto
        );

        commentRequestDto = new CommentRequestDto(
                "Test Comment content"
        );

        comment = new Comment(
                commentRequestDto.getContent(),
                board,
                user
        );
    }

    @Test
    @DisplayName("Comment 생성 테스트")
    void test1() {
        // Given
        // setUp() 에서 진행됨

        // When
        // 객체가 생성되면서 진행됨

        // Then
        assertThat(comment.getContent()).isEqualTo(commentRequestDto.getContent());
        assertThat(comment.getLikeCount()).isEqualTo(0L);
        assertThat(comment.getBoard()).isNotNull();
        assertThat(comment.getUser()).isNotNull();
    }

    @Test
    @DisplayName("update - 댓글 수정 테스트")
    void test2() {
        // Given
        CommentRequestDto updateDto = new CommentRequestDto(
                "Update Test Content"
        );

        // When
        comment.update(updateDto);

        // Then
        assertThat(comment.getContent()).isEqualTo(updateDto.getContent());
    }

    @Test
    @DisplayName("delete - 댓글 삭제 테스트")
    void test3() {
        // Given

        // When
        comment.delete();

        // Then
        assertThat(comment.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("isCommentAuthor - 댓글 작성자 테스트")
    void test4() {
        // Given
        long userId1 = 1L;
        long userId2 = 2L;
        when(user.getId()).thenReturn(1L);

        // When
        boolean isAuthorTrue = comment.isCommentAuthor(userId1);
        boolean isAuthorFalse = comment.isCommentAuthor(userId2);

        // Then
        assertThat(isAuthorTrue).isTrue();
        assertThat(isAuthorFalse).isFalse();
    }
}