package com.sparta.areadevelopment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.areadevelopment.dto.BoardRequestDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardTest {

    private Board board;
    private User user;
    private BoardRequestDto requestDto;

    @BeforeEach
    @DisplayName("Board 생성")
    void setUp() {
        user = new User(
                "testUser",
                "testNickname",
                "Abcde12345!",
                "testuser123@email.com",
                "test User Info"
        );

        requestDto = new BoardRequestDto();
        requestDto.setTitle("테스트 제목");
        requestDto.setContent("테스트 내용");

        board = new Board(
                user,
                requestDto
        );
    }

    @Test
    @DisplayName("Board 생성 테스트")
    void test1() {
        // Given
        // setUp() 에서 주어짐

        // When
        // 객체가 생성되면서 진행됨

        // Then
        assertThat(board.getUser()).isEqualTo(user);
        assertThat(board.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(board.getContent()).isEqualTo(requestDto.getContent());
        assertThat(board.getHits()).isEqualTo(0L);
        assertThat(board.getLikeCount()).isEqualTo(0L);
        assertThat(board.getModifiedAt()).isNotNull();
        assertThat(board.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("hitsUp - 조회수 테스트")
    void test2() {
        // Given

        // When
        board.hitsUp();

        // Then
        assertThat(board.getHits()).isEqualTo(1L);
    }

    @Test
    @DisplayName("update - 업데이트 테스트")
    void test3() {
        // Given
        BoardRequestDto updateRequestDto = new BoardRequestDto();
        updateRequestDto.setTitle("Update Test Title");
        updateRequestDto.setContent("Update Test Content");

        LocalDateTime beforeUpdate = board.getModifiedAt();

        // When
        board.update(updateRequestDto);

        // Then
        assertThat(board.getTitle()).isEqualTo(updateRequestDto.getTitle());
        assertThat(board.getContent()).isEqualTo(updateRequestDto.getContent());
        assertThat(board.getModifiedAt()).isNotNull();
        assertThat(board.getModifiedAt()).isAfter(beforeUpdate);
    }
}