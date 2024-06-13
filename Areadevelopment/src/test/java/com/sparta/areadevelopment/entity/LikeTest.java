package com.sparta.areadevelopment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.areadevelopment.enums.LikeTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeTest {

    @Mock
    private User mockUser;

    private Like like;
    private Long contentId;
    private LikeTypeEnum contentType;

    @Test
    @DisplayName("Like 생성 (board)")
    void test1() {
        // Given
        contentId = 1L;
        contentType = LikeTypeEnum.BOARD;

        // When
        like = new Like(mockUser, contentId, contentType);

        // Then
        assertThat(like.getUser()).isEqualTo(mockUser);
        assertThat(like.getContentId()).isEqualTo(contentId);
        assertThat(like.getContentType()).isEqualTo(contentType);
        assertThat(like.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Like 생성 (comment)")
    void test2() {
        // Given
        contentId = 2L;
        contentType = LikeTypeEnum.COMMENT;

        // When
        like = new Like(mockUser, contentId, contentType);

        // Then
        assertThat(like.getUser()).isEqualTo(mockUser);
        assertThat(like.getContentId()).isEqualTo(contentId);
        assertThat(like.getContentType()).isEqualTo(contentType);
        assertThat(like.getCreatedAt()).isNotNull();
    }
}