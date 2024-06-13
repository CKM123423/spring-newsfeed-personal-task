package com.sparta.areadevelopment.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentRequestDto {

    /**
     * 댓글의 내용입니다.
     */
    private String content;

    @Builder
    public CommentRequestDto(String content) {
        this.content = content;
    }
}
