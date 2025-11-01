package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.BoardRequestException;
import com.karishma.worksphere.exception.MemberOnlyException;
import com.karishma.worksphere.exception.UserNotFoundException;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.model.enums.BoardRole;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.model.enums.Status;
import com.karishma.worksphere.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardRequestService {

    private final AuthRepository authRepository;
    private final BoardRequestRepository boardRequestRepository;
   private final BoardMemberRepository boardMemberRepository;
   private final BoardRepository boardRepository;
    public ResponseEntity<?> createRequest(BoardRequestDTO request, String email) {

        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Authentication not found"));

        BoardRequest boardRequest = BoardRequest.builder()
                .board_request_name(request.getBoard_name())
                .board_request_key(request.getBoard_key())
                .requester(auth.getUser())
                .description(request.getDescription())
                .justification(request.getJustification())
                .build();

        boardRequestRepository.save(boardRequest);

        return ResponseEntity
                .status(201)
                .body(Map.of(
                        "message", "Board created successfully",
                        "boardName", boardRequest.getBoard_request_name(),
                        "boardId",boardRequest.getBoard_request_id(),
                        "requestedBy", auth.getEmail()
                ));
    }
    public List<BoardRequest> getAllBoardRequests(){
        return boardRequestRepository.findAll();
    }
    @Transactional
    public void approveRequest(@PathVariable UUID id) {
        BoardRequest boardRequest = boardRequestRepository.findById(id)
                .orElseThrow(() -> new BoardRequestException("Board request not found with id: " + id));

        if (!Status.PENDING.equals(boardRequest.getStatus())) {
            throw new BoardRequestException("This request has already been " + boardRequest.getStatus());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated") {};
        }

        Auth authAdmin = authRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Admin not found with email: " + auth.getName()));
        User admin = authAdmin.getUser();

        Board board = Board.builder()
                .board_name(boardRequest.getBoard_request_name())
                .board_key(boardRequest.getBoard_request_key())
                .description(boardRequest.getDescription())
                .createdBy(boardRequest.getRequester())
                .build();
        boardRepository.save(board);

        BoardMember boardMember = BoardMember.builder()
                .board(board)
                .user(board.getCreatedBy())
                .boardRole(BoardRole.PROJECT_ADMIN)
                .build();
        boardMemberRepository.save(boardMember);

        boardRequest.setStatus(Status.APPROVED);
        boardRequest.setApprovedAt(LocalDateTime.now());
        boardRequest.setReviewedBy(admin);
    }
}
