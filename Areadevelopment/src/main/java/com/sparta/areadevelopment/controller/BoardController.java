package com.sparta.areadevelopment.controller;

import com.sparta.areadevelopment.dto.BoardRequestDto;
import com.sparta.areadevelopment.dto.BoardResponseDto;
import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.service.BoardService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 뉴스피드 컨트롤러 조회를 제외하고는 모두 User의 정보가 필요하다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BoardController {

    /**
     * 보드 서비스
     */
    private final BoardService boardService;

    /**
     * 보드 생성 controller
     *
     * @param userDetails
     * @param requestDto
     * @return
     */
    @PostMapping("/boards")
    public BoardResponseDto createBoard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BoardRequestDto requestDto) {
        return boardService.createBoard(userDetails.getUser(), requestDto);
    }

    /**
     * 뉴스피드 내용 불러오기
     */
    @GetMapping("/boards")
    public ResponseEntity<?> findAllBoard() {
        List<BoardResponseDto> list = boardService.findAllBoard();
        if (list.isEmpty()) {
            return ResponseEntity.ok().body("먼저 작성하여 소식을 알려보세요!");
        } else {
            return ResponseEntity.ok().body(list);
        }
    }

    // 10개씩 페이지네이션하여, 각 페이지 당 뉴스피드 데이터가 10개씩 최신순으로 나오게 합니다.
    @GetMapping("/boards/recently/{page}")
    public ResponseEntity<?> findAllRecentlyPagination(@PathVariable int page) {
        // ex) 1페이지 조회시 -> index는 0으로 들어가므로 -1을 해줌
        List<BoardResponseDto> list = boardService.findAllRecentlyPagination(page - 1);
        if (list.isEmpty()) {
            return ResponseEntity.ok().body("먼저 작성하여 소식을 알려보세요!");
        } else {
            return ResponseEntity.ok().body(list);
        }
    }

    // 좋아요 개수가 많은 순서대로 정렬 (페이지 당 뉴스피드 데이터 = 10개 고정)
    @GetMapping("/boards/like/{page}")
    public ResponseEntity<?> findAllLikesPagination(@PathVariable int page) {
        List<BoardResponseDto> list = boardService.findAllLikesPagination(page - 1);
        if (list.isEmpty()) {
            return ResponseEntity.ok().body("먼저 작성하여 소식을 알려보세요!");
        } else {
            return ResponseEntity.ok().body(list);
        }
    }

    /**
     * 기간별 조회 ex ) String startTime = 2024-05-07 이런식으로 넣어서 테스트 합니다.
     */
    @GetMapping("/boards/date/{page}")
    public ResponseEntity<?> findAllDatePagination(
            @PathVariable int page,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        List<BoardResponseDto> list = boardService.findAllDatePagination(page - 1, startTime,
                endTime);

        if (list.isEmpty()) {
            return ResponseEntity.ok().body("먼저 작성하여 소식을 알려보세요!");
        } else {
            return ResponseEntity.ok().body(list);
        }
    }

    @GetMapping("/boards/{boardId}")
    public BoardResponseDto findBoard(@PathVariable Long boardId) {
        return boardService.findBoard(boardId);
    }

    /**
     * 뉴스피드 수정
     *
     * @param userDetails
     * @param requestDto
     * @param boardId
     * @return
     */
    @PutMapping("/boards/{boardId}")
    public BoardResponseDto updateBoard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BoardRequestDto requestDto,
            @PathVariable Long boardId) {

        return boardService.updateBoard(userDetails.getUser(), requestDto, boardId);
    }

    /**
     * 뉴스피드 삭제
     *
     * @param userDetails
     * @param boardId
     * @return
     */
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<String> deleteBoard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long boardId) {
        boardService.deleteBoard(userDetails.getUser(), boardId);
        return ResponseEntity.ok().body("게시글이 삭제 되었습니다.");
    }
}