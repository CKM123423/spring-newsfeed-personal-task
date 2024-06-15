package com.sparta.areadevelopment.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.sparta.areadevelopment.dto.BoardRequestDto;
import com.sparta.areadevelopment.entity.Board;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.repository.BoardRepository;
import com.sparta.areadevelopment.repository.CommentRepository;
import com.sparta.areadevelopment.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;


    @Test
    @DisplayName("findBoard - Board Not Found Fail Test")
    void findBoard_Not_Found_Fail_Test() {
        // Given
        Long boardId = 20000L;
        when(boardRepository.findByIdAndDeletedAtIsNull(boardId)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.findBoard(boardId);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("해당 게시글은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("updateBoard - Board Not Found Fail Test")
    void updateBoard_Not_Found_Fail_Test() {
        // Given
        Long boardId = 20000L;
        given(boardRepository.findByIdAndDeletedAtIsNull(boardId)).willReturn(Optional.empty());

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.findBoard(boardId);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("해당 게시글은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("updateBoard - Not Same Writer Fail Test")
    void updateBoard_Not_Same_Writer_Fail_Test() {
        // Given
        User boardWriterUser = Mockito.mock(User.class);
        given(boardWriterUser.getId()).willReturn(1L);

        Board board = Mockito.mock(Board.class);
        given(board.getId()).willReturn(1L);
        given(board.getUser()).willReturn(boardWriterUser);

        User user = Mockito.mock(User.class);
        given(user.getId()).willReturn(2L);

        BoardRequestDto requestDto = Mockito.mock(BoardRequestDto.class);

        given(boardRepository.findByIdAndDeletedAtIsNull(board.getId())).willReturn(
                Optional.of(board));

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.updateBoard(user, requestDto, board.getId());
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("작성자만 수정 가능합니다.");
    }

    @Test
    @DisplayName("deleteBoard - Board Not Found Fail Test")
    void deleteBoard_Not_Found_Fail_Test() {
        // Given
        User user = Mockito.mock(User.class);

        Long boardId = 20000L;
        given(boardRepository.findByIdAndDeletedAtIsNull(boardId)).willReturn(Optional.empty());

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.deleteBoard(user, boardId);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("해당 게시글은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("deleteBoard - Not Same Writer Fail Test")
    void deleteBoard_Not_Same_Writer_Fail_Test() {
        // Given
        Long boardId = 1L;

        User boardWriterUser = Mockito.mock(User.class);
        given(boardWriterUser.getId()).willReturn(1L);

        Board board = Mockito.mock(Board.class);
        given(board.getId()).willReturn(1L);
        given(board.getUser()).willReturn(boardWriterUser);

        User user = Mockito.mock(User.class);
        given(user.getId()).willReturn(2L);

        given(boardRepository.findByIdAndDeletedAtIsNull(board.getId())).willReturn(
                Optional.of(board));

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.deleteBoard(user, boardId);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("작성자만 삭제 가능합니다.");
    }
}
