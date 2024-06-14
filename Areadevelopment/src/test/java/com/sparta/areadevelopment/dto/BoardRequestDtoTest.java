package com.sparta.areadevelopment.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 이 부분은 실제로 컨트롤러에서 @Valid 를 썻을 때 검증되니 컨트롤러에서 테스트.
 */
class BoardRequestDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;
    private static ObjectMapper objectMapper;

    private BoardRequestDto requestDto;

    @BeforeEach
    @DisplayName("검증 필요 객체 주입")
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Dto - 생성 성공 테스트")
    void test1() throws Exception {
        // Given
        String json = "{\"title\":\"Board Title\", \"content\":\"Board Content\"}";

        // When
        requestDto = objectMapper.readValue(json, BoardRequestDto.class);

        // Then
        Set<ConstraintViolation<BoardRequestDto>> violations = validator.validate(requestDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Dto - 제목 누락 테스트")
    void test2() throws Exception {
        // Given
        String json = "{\"title\":\"\", \"content\":\"Board Content\"}";

        // When
        requestDto = objectMapper.readValue(json, BoardRequestDto.class);

        // Then
        Set<ConstraintViolation<BoardRequestDto>> violations = validator.validate(requestDto);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("제목을 입력해주세요.");
    }

    @Test
    @DisplayName("Dto - 본문 누락 테스트")
    void test3() throws Exception {
        // Given
        String json = "{\"title\":\"Board Title\", \"content\":\"\"}";

        // When
        requestDto = objectMapper.readValue(json, BoardRequestDto.class);

        // Then
        Set<ConstraintViolation<BoardRequestDto>> violations = validator.validate(requestDto);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("내용을 입력해주세요.");
    }

    @Test
    @DisplayName("Dto - 전부 누락 테스트")
    void test4() throws Exception {
        // Given
        String json = "{\"title\":\"\", \"content\":\"\"}";

        // When
        requestDto = objectMapper.readValue(json, BoardRequestDto.class);

        // Then
        Set<ConstraintViolation<BoardRequestDto>> violations = validator.validate(requestDto);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(2);

        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("제목을 입력해주세요.")));
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("내용을 입력해주세요.")));
    }
}