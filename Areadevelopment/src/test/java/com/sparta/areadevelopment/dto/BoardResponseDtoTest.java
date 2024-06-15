package com.sparta.areadevelopment.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.sparta.areadevelopment.entity.Board;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 이 부분은 그저 생성자 테스트이고 안에 로직이 없으니 만들필요가 없다고 생각하고 후에 필드가 바뀐다면 유지보수 비용이 2배 이므로 하지 않는게 좋다고 생각함.
 */
@ExtendWith(MockitoExtension.class)
public class BoardResponseDtoTest {

    @Mock
    private Board board;

    @BeforeEach
    @DisplayName("Board 가짜 객체 생성")
    void setUp() {
        when(board.getId()).thenReturn(1L);
        when(board.getTitle()).thenReturn("Test Title");
        when(board.getContent()).thenReturn("Test Content");
        when(board.getHits()).thenReturn(0L);
        when(board.getLikeCount()).thenReturn(0L);
        when(board.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(board.getModifiedAt()).thenReturn(LocalDateTime.now());
    }

    @Test
    @DisplayName("Dto - 생성자 테스트")
    void constructorTest() {
        // Given
        // setUp() 에서 주어짐

        // When
        BoardResponseDto responseDto = new BoardResponseDto(board);

        // Then
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getTitle()).isEqualTo("Test Title");
        assertThat(responseDto.getContent()).isEqualTo("Test Content");
        assertThat(responseDto.getHits()).isEqualTo(0L);
        assertThat(responseDto.getLikeCount()).isEqualTo(0L);
        assertThat(responseDto.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(responseDto.getModifiedAt()).isBefore(LocalDateTime.now());
    }
}