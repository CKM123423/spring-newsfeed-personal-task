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
        assertThat(responseDto.getCount()).isEqualTo(0L);
        assertThat(responseDto.getCreateAt()).isBefore(LocalDateTime.now());
        assertThat(responseDto.getModifiedAt()).isBefore(LocalDateTime.now());
    }
}