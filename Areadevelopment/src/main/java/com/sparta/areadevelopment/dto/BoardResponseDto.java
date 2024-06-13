package com.sparta.areadevelopment.dto;

import com.sparta.areadevelopment.entity.Board;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스피드의 정보를 보여주는 DTO
 */
@NoArgsConstructor
@Getter
public class BoardResponseDto {

    /**
     * DTO의 필드
     *
     * @Long id    뉴스피드의 pk값
     * @String title   뉴스피드의 제목
     * @String content 뉴스피드의 내용
     * @Long hits    조회수
     * @Long count   좋아요수
     * @LocalDateTime createAt    생성일
     * @LocalDateTime modifiedAt  수정일자
     */
    private Long id;
    private String title;
    private String content;
    private Long hits;
    private Long count;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

    /**
     * DTO의 생성자 매서드
     */
    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.hits = board.getHits();
        this.count = board.getLikeCount();
        this.createAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
    }
}
