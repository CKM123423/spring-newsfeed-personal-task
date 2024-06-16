package com.sparta.areadevelopment.service;

import com.sparta.areadevelopment.dto.CommentRequestDto;
import com.sparta.areadevelopment.dto.CommentResponseDto;
import com.sparta.areadevelopment.entity.Board;
import com.sparta.areadevelopment.entity.Comment;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.repository.BoardRepository;
import com.sparta.areadevelopment.repository.CommentRepository;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    /**
     * 댓글의 아이디와 삭제상태를
     *
     * @param user       토큰 인증이 완료된 유저객체
     * @param boardId    게시판 고유번호
     * @param requestDto 사용자가 등록 요청한 정보
     * @return 댓글의 상세 정보
     */
    public CommentResponseDto addComment(User user, Long boardId,
            CommentRequestDto requestDto) {

        Board board = getActiveBoardById(boardId);
        Comment comment = new Comment(requestDto.getContent(), board, user);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    /**
     * 게시글에 종속된 삭제되지 않은 댓글들을 내역을 내림차순으로 정렬하려 보여줍니다.
     *
     * @param boardId 게시글 고유번호
     * @return 댓글의 상세정보 모음
     */
    public List<CommentResponseDto> getAllComments(Long boardId) {
        return commentRepository.findByDeletedAtNullAndBoardIdOrderByCreatedAtDesc(boardId)
                .map(Collection::stream).orElseGet(Stream::empty).map(CommentResponseDto::new)
                .toList();
    }

    /**
     * 사용자를 검증하여 게시글에 등록된 댓글의 내용을 수정합니다.
     *
     * @param userId     유저 고유번호
     * @param commentId  댓글 고유번호
     * @param requestDto 사용자가 수정을 요청한 내용
     * @param boardId    게시글 고유번호
     * @return 댓글 상세 정보
     */
    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId,
            CommentRequestDto requestDto) {
        Comment comment = getActiveCommentById(commentId);

        if (!comment.isCommentAuthor(userId)) {
            throw new IllegalArgumentException("선택한 댓글은 다른 사용자가 작성한 댓글입니다.");
        }
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    /**
     * 사용자를 검증하여 게시글에 등록된 댓글을 논리삭제 합니다.
     *
     * @param userId    사용자 고유번호
     * @param commentId 댓글 고유번호
     * @param boardId
     * @return 댓글 삭제 성공 문장
     */
    @Transactional
    public String deleteComment(Long userId, Long commentId) {
        Comment comment = getActiveCommentById(commentId);

        if (!comment.isCommentAuthor(userId)) {
            throw new IllegalArgumentException("선택한 댓글은 다른 사용자가 작성한 댓글입니다.");
        }
        comment.softDelete();
        return "댓글 삭제 성공";
    }

    /**
     * 삭제되지 않은 유효한 게시글을 고유번호로 찾아옵니다.
     *
     * @param boardId 게시글 고유번호
     * @return 게시글 객체
     */
    private Board getActiveBoardById(Long boardId) {
        return boardRepository.findByIdAndDeletedAtIsNull(boardId)
                .orElseThrow(() -> new NullPointerException("선택한 게시물은 없거나 삭제되었습니다."));
    }

    /**
     * 삭제되지 않은 유효한 댓글을 고유번호로 찾아옵니다.
     *
     * @param commentId 댓글 고유번호
     * @return 댓글 객체
     */
    private Comment getActiveCommentById(Long commentId) {
        return commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new NullPointerException("선택한 댓글은 없거나 삭제되었습니다."));
    }
}
