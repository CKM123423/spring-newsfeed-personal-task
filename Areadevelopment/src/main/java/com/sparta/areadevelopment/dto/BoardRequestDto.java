package com.sparta.areadevelopment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 뉴스피드의 불러오는 정보를 담는 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {

    /**
     * @String title 뉴스피드의 제목
     */
    @NotBlank(message = "제목을 입력해주세요.")
    String title;

    /**
     * @String content 뉴스피드의 내용
     */
    @NotBlank(message = "내용을 입력해주세요.")
    String content;
}
