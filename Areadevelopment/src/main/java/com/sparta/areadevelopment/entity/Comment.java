package com.sparta.areadevelopment.entity;

import com.sparta.areadevelopment.dto.CommentRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment(String content, Board board, User user) {
        this.content = content;
        this.likeCount = 0L;
        this.board = board;
        this.user = user;
    }

    public void update(CommentRequestDto dto) {
        this.content = dto.getContent();
    }

    public void softDelete() {
        setDeletedAt(LocalDateTime.now());
    }

    public boolean isCommentAuthor(Long userId) {
        if (this.user.getId().equals(userId)) {
            return true;
        }
        return false;
    }
}
