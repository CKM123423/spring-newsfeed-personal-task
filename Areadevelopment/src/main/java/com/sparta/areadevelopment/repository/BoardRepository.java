package com.sparta.areadevelopment.repository;

import com.sparta.areadevelopment.entity.Board;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {


    Optional<Board> findByIdAndDeletedAtIsNull(Long id);

    // DeletedAt이 Not Null 인 경우 삭제된 게시글
    List<Board> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    // 최신순으로 Paging
    Page<Board> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    // 좋아요순으로 Paging
    Page<Board> findAllByDeletedAtIsNullOrderByLikeCountDesc(Pageable pageable);

    // 기간별 Paging
    Page<Board> findAllByDeletedAtIsNullAndCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    /**
     * 좋아요 내역이 저장되었을때 게시판 좋아요 필드값을 증가시키는 쿼리문입니다.
     *
     * @param boardId 게시판 고유번호
     */
    @Modifying
    @Query("UPDATE Board b SET b.likeCount = b.likeCount + 1 WHERE b.id = :boardId")
    void incrementLikeCount(@Param("boardId") Long boardId);

    /**
     * 좋아요 내역이 삭제되었을때 게시판 좋아요 필드값을 감소시키는 쿼리문입니다.
     *
     * @param boardId 고유아이디
     */
    @Modifying
    @Query("UPDATE Board b SET b.likeCount = b.likeCount - 1 WHERE b.id = :boardId")
    void decrementLikeCount(@Param("boardId") Long boardId);


    List<Board> findByUserIdAndDeletedAtIsNull(Long id);
}
