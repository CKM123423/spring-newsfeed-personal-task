package com.sparta.areadevelopment.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.sparta.areadevelopment.dto.CommentRequestDto;
import com.sparta.areadevelopment.entity.Comment;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.repository.BoardRepository;
import com.sparta.areadevelopment.repository.CommentRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("addComment In getActiveBoardById - Board Not Found Fail Test")
    void addComment_getActiveBoardById_Not_Found_Test() {
        // Given
        Long boardId = 20000L;
        given(boardRepository.findByIdAndDeletedAtIsNull(boardId)).willReturn(Optional.empty());
        User user = Mockito.mock(User.class);
        CommentRequestDto requestDto = Mockito.mock(CommentRequestDto.class);

        // When private 의 단위테스트
        Exception exception = assertThrows(NullPointerException.class, () -> {
            commentService.addComment(user, boardId, requestDto);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("선택한 게시물은 없거나 삭제되었습니다.");
    }

    @Test
    @DisplayName("updateComment In getActiveCommentById - Comment Not Found Fail Test")
    void updateComment_getActiveCommentById_Not_Found_Test() {
        // Given
        Long userId = 1L;
        Long commentId = 20000L;
        CommentRequestDto requestDto = Mockito.mock(CommentRequestDto.class);
        given(commentRepository.findByIdAndDeletedAtIsNull(commentId)).willReturn(Optional.empty());

        // When
        Exception exception = assertThrows(NullPointerException.class, () -> {
            commentService.updateComment(userId, commentId, requestDto);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("선택한 댓글은 없거나 삭제되었습니다.");
    }

    @Test
    @DisplayName("updateComment In isCommentAuthor - Fail Test")
    void updateComment_isCommentAuthor_Fail_Test() {
        // Given
        Long userId = 1L;
        Long commentId = 20000L;
        Comment comment = Mockito.mock(Comment.class);
        CommentRequestDto requestDto = Mockito.mock(CommentRequestDto.class);
        given(commentRepository.findByIdAndDeletedAtIsNull(commentId)).willReturn(
                Optional.of(comment));

        given(comment.isCommentAuthor(userId)).willReturn(false);

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.updateComment(userId, commentId, requestDto);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("선택한 댓글은 다른 사용자가 작성한 댓글입니다.");
    }

    @Test
    @DisplayName("deleteComment In isCommentAuthor - Fail Test")
    void softDeleteComment_isCommentAuthor_Fail_Test() {
        // Given
        Long userId = 1L;
        Long commentId = 20000L;
        Comment comment = Mockito.mock(Comment.class);
        given(commentRepository.findByIdAndDeletedAtIsNull(commentId)).willReturn(
                Optional.of(comment));

        given(comment.isCommentAuthor(userId)).willReturn(false);

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.deleteComment(userId, commentId);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("선택한 댓글은 다른 사용자가 작성한 댓글입니다.");
    }
}
