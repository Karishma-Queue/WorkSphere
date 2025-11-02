package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.BoardRequestException;
import com.karishma.worksphere.exception.MemberOnlyException;
import com.karishma.worksphere.exception.UserNotFoundException;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.dto.request.BoardRequestUpdateDTO;
import com.karishma.worksphere.model.dto.request.RejectRequestDTO;
import com.karishma.worksphere.model.dto.response.BoardRequestResponse;
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
                .board_request_name(request.getBoard_request_name())
                .board_request_key(request.getBoard_request_key())
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
                        "boardId", boardRequest.getBoard_request_id(),
                        "requestedBy", auth.getEmail()
                ));
    }

    public List<BoardRequest> getAllBoardRequests() {
        return boardRequestRepository.findAll();
    }

    @Transactional
    public void approveRequest(@PathVariable UUID id) {
        BoardRequest boardRequest = boardRequestRepository.findById(id)
                .orElseThrow(() -> new BoardRequestException("Board request not found with id: " + id));

        if (!Status.PENDING.equals(boardRequest.getStatus())) {
            throw new BoardRequestException("This request has already been " + boardRequest.getStatus());
        }

        User admin = authUser(new AuthenticationException("User not authenticated") {
        });

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

    @Transactional
    public void rejectRequest(@PathVariable UUID id, RejectRequestDTO request) {
        BoardRequest boardRequest = boardRequestRepository.findById(id)
                .orElseThrow(() -> new BoardRequestException("Board request not found with id: " + id));

        if (!Status.PENDING.equals(boardRequest.getStatus())) {
            throw new BoardRequestException("This request has already been " + boardRequest.getStatus());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new org.springframework.security.core.AuthenticationException("User not authenticated") {
            };
        }

        Auth authAdmin = authRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Admin not found with email: " + auth.getName()));
        User admin = authAdmin.getUser();

        boardRequest.setReviewedBy(admin);
        boardRequest.setRejectedAt(LocalDateTime.now());
        boardRequest.setStatus(Status.REJECTED);

        if (request != null && request.getRejection_reason() != null) {
            boardRequest.setRejection_reason(request.getRejection_reason());
        }
    }

    public List<BoardRequestResponse> myAllRequests() {
        User member = authUser(new AuthenticationException("User not authenticated"));
        List<BoardRequest> boardRequestList= boardRequestRepository.findByRequester(member);
        return boardRequestList.stream()
                .map(this::mapToDTO)
                .toList();

    }

    private User authUser(AuthenticationException User_not_authenticated) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw User_not_authenticated;
        }
        Auth authMember = authRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Admin not found with email: " + auth.getName()));
        User member = authMember.getUser();
        return member;
    }

    private BoardRequestResponse mapToDTO(BoardRequest entity) {
        BoardRequestResponse dto = new BoardRequestResponse();
        dto.setId(entity.getBoard_request_id());
        dto.setName(entity.getBoard_request_name());
        dto.setRequesterName(entity.getRequester());
        return dto;
    }
    public List<BoardRequestResponse> myRequestsByStatus(Status status)
    {
        User member = authUser(new AuthenticationException("User not authenticated"));
        List<BoardRequest> boardRequestList= boardRequestRepository.findByRequesterAndStatus(member,status);
        return boardRequestList.stream()
                .map(this::mapToDTO)
                .toList();

    }
  public void updateMyRequest(UUID id, BoardRequestUpdateDTO request)
  {
      BoardRequest boardRequest=boardRequestRepository.findById(id)
              .orElseThrow()->new
  }

}