package com.sparta.areadevelopment.dto;


import com.sparta.areadevelopment.entity.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    /**
     * 댓글의 고유번호입니다.
     */
    private Long id;

    /**
     * 게시판의 고유번호입니다.
     */
    private Long boardId;


    /**
     * 댓글의 내용입니다.
     */
    private String content;

    /**
     * 댓글의 좋아요 수입니다.
     */
    private Long likeCount;

    /**
     * 댓글의 생성시간입니다.
     */
    private LocalDateTime createdAt;

    /**
     * 댓글의 수정시간입니다.
     */
    private LocalDateTime modifiedAt;

    /**
     * 객체의 생성자입니다.
     *
     * @param comment 댓글 Entity 입니다.
     */
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.boardId = comment.getBoard().getId();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
