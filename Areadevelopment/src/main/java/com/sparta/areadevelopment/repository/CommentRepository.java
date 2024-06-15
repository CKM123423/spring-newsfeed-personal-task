package com.sparta.areadevelopment.repository;


import com.sparta.areadevelopment.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 생성일자 내림차순으로 게시판 하나의 종속된 삭제되지않은 모든 댓글을 조회합니다.
     *
     * @param boardId 게시글 고유번호
     * @return 댓글 상세정보 목록
     */
    Optional<List<Comment>> findByDeletedAtNullAndBoardIdOrderByCreatedAtDesc(Long boardId);

    /**
     * 좋아요 내역이 저장되었을때 댓글 좋아요 필드값을 증가시키는 쿼리문입니다.
     *
     * @param commentId 댓글 고유번호
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :commentId")
    void incrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 좋아요 내역이 삭제되었을때 게시판 좋아요 필드값을 감소시키는 쿼리문입니다.
     *
     * @param commentId 댓글 고유번호
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount - 1 WHERE c.id = :commentId")
    void decrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 삭제되지않은 댓글 하나의 정보를 불러옵니다.
     *
     * @param commentId 댓글 고유번호
     * @return 댓글 하나의 정보
     */
    Optional<Comment> findByIdAndDeletedAtNull(Long commentId);

    /**
     * 삭제 처리 되어있지않고 해당 유저가 작성한? ????????????????????????????????????????????
     *
     * @param boardId
     * @param userId
     * @return
     */
    List<Comment> findByBoardIdAndDeletedAtIsNull(Long boardId);
}
