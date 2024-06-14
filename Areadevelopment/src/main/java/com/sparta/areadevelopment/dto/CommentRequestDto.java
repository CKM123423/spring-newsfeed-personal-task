package com.sparta.areadevelopment.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    /**
     * 댓글의 내용입니다.
     */
    @NotBlank
    private String content;
}
