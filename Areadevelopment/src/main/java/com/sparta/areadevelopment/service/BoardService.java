package com.sparta.areadevelopment.service;

import com.sparta.areadevelopment.dto.BoardRequestDto;
import com.sparta.areadevelopment.dto.BoardResponseDto;
import com.sparta.areadevelopment.entity.Board;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.repository.BoardRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    /**
     * 1. deletedAt = Null 인 경우에만 조회가 가능합니다. (삭제되는 순간 LocalDateTime.now()로 변경됩니다. 2.
     *
     * @AuthenticationPrincipal을 통해 User 정보를 받아 온 후 검증합니다. 3. board와 User은 다:1 , board와 comment는 1:다
     * 관계로 맺어줬습니다.
     */
    private final BoardRepository boardRepository;

    public BoardResponseDto createBoard(User user, BoardRequestDto requestDto) {
        Board board = boardRepository.save(new Board(user, requestDto));
        return new BoardResponseDto(board);
    }

    // 모든 페이지 조회, 글이 있을 경우 ApiResponseDto 의 data 조회
    public List<BoardResponseDto> findAllBoard() {
        return boardRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(BoardResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    // 최신순으로 10개씩 페이지네이션하는 Service 로직 추가.
    public List<BoardResponseDto> findAllRecentlyPagination(int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        return boardRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(
                        pageable)
                .stream()
                .map(BoardResponseDto::new).toList();
    }

    // 좋아요 많은 순으로 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> findAllLikesPagination(int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        return boardRepository.findAllByDeletedAtIsNullOrderByLikeCountDesc(
                        pageable)
                .stream()
                .map(BoardResponseDto::new).toList();
    }

    // 입력받은 기간 사이에 생성된 게시글들만 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> findAllDatePagination(
            int page,
            String startTime,
            String endTime) {
        // LocalDate.parse를 사용하여 문자열을 LocalDate로 파싱
        LocalDate startDate = LocalDate.parse(startTime);
        LocalDate endDate = LocalDate.parse(endTime);

        // LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        return boardRepository.findAllByDeletedAtIsNullAndCreatedAtBetweenOrderByCreatedAtDesc
                        (startDateTime, endDateTime, pageable)
                .stream()
                .map(BoardResponseDto::new).toList();
    }

    @Transactional
    public BoardResponseDto findBoard(Long boardId) {

        Board board = boardRepository.findByIdAndDeletedAtIsNull(boardId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        // 조회수 + 1
        board.hitsUp();

        return new BoardResponseDto(board);
    }

    @Transactional
    public BoardResponseDto updateBoard(User user, BoardRequestDto requestDto, Long boardId) {
        Board board = boardRepository.findByIdAndDeletedAtIsNull(boardId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        // 같은 사용자만 수정 가능
        if (!Objects.equals(board.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("작성자만 수정 가능합니다.");
        }

        board.update(requestDto);
        return new BoardResponseDto(board);
    }


    @Transactional
    public void deleteBoard(User user, Long boardId) {
        Board board = boardRepository.findByIdAndDeletedAtIsNull(boardId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        if (!Objects.equals(board.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("작성자만 삭제 가능합니다.");
        }

        // 삭제시간 저장
        board.setDeletedAt(board.getModifiedAt());
        log.info(board.getDeletedAt().toString());
    }

}