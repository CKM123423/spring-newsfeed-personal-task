package com.sparta.areadevelopment.controller;


import com.sparta.areadevelopment.dto.CommentRequestDto;
import com.sparta.areadevelopment.dto.CommentResponseDto;
import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 이 Controller 게시글에 종속된 댓글의 요청을 받고 반환합니다.
 */
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class CommentController {

    /**
     * DTO의 생성자 매서드
     */
    private final CommentService commentService;

    /**
     * 게시글 고유번호를 받아 해당하는 게시글에 종속되는 댓글을 작성합니다.
     *
     * @param userDetails 토큰 인증 통과후 시큐리티 컨텍스트에 저장된 유저의 정보
     * @param boardId     게시글의 고유번호
     * @param requestDto  댓글 내용
     * @return 상태코드 200과 댓글 상세정보를 반환합니다.
     */
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long boardId, @Valid @RequestBody CommentRequestDto requestDto) {
        return ResponseEntity.ok().body(commentService.addComment(userDetails.getUser(), boardId,
                requestDto));
    }

    /**
     * 게시글 하나에 종속된 모든 댓글을 조회하고 댓글이 없다면 댓글 작성 문구를 보여줍니다.
     *
     * @param boardId 게시글 고유번호
     * @return 댓글의 작성 문구를 보여주거나 댓글의 내용을 보여줍니다.
     */
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<?> getAllComments(@PathVariable Long boardId) {
        List<CommentResponseDto> comments = commentService.getAllComments(boardId);
        if (comments.isEmpty()) {
            return ResponseEntity.ok("먼저 작성하여 댓글을 남겨보세요!");
        } else {
            return ResponseEntity.ok(comments);
        }
    }

    /**
     * 게시글 하나에 종속된 하나의 특정 댓글의 사용자를 검증하여 일치하면 내용을 수정 합니다.
     *
     * @param userDetails 토큰 인증 통과후 시큐리티 컨텍스트에 저장된 유저의 정보
     * @param commentId   댓글 고유번호
     * @param requestDto  변경 요청 내용
     * @return 수정완료된 댓글의 정보
     */
    @PutMapping("/{boardId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId, @Valid @RequestBody CommentRequestDto requestDto) {
        return ResponseEntity.ok()
                .body(commentService.updateComment(userDetails.getUser().getId(), commentId,
                        requestDto));
    }

    /**
     * 게시글 하나에 종속된 하나의 특정 댓글의 사용자를 검증하여 일치하면 내용을 삭제 합니다.
     *
     * @param userDetails 토큰 인증 통과후 시큐리티 컨텍스트에 저장된 유저의 정보
     * @param commentId   댓글 고유번호
     * @return 댓글 삭제 완료 문구
     */
    @DeleteMapping("/{boardId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentId) {
        return ResponseEntity.ok(
                commentService.deleteComment(userDetails.getUser().getId(), commentId));
    }
}
