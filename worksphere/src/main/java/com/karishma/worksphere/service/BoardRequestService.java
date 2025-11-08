package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.*;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.dto.request.BoardRequestUpdateDTO;
import com.karishma.worksphere.model.dto.request.RejectRequestDTO;
import com.karishma.worksphere.model.dto.response.BoardDetailsDTO;
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


    public void approveRequest( UUID id) {
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
        boardRequestRepository.save(boardRequest);
    }

    public void rejectRequest(UUID id, RejectRequestDTO request) {
        BoardRequest boardRequest = boardRequestRepository.findById(id)
                .orElseThrow(() -> new BoardRequestException("Board request not found with id: " + id));

        if (!Status.PENDING.equals(boardRequest.getStatus())) {
            throw new BoardRequestException("This request has already been " + boardRequest.getStatus());
        }

        User admin = authUser(new AuthenticationException("User not authenticated") {
        });

        boardRequest.setReviewedBy(admin);
        boardRequest.setRejectedAt(LocalDateTime.now());
        boardRequest.setStatus(Status.REJECTED);

        if (request != null && request.getRejection_reason() != null) {
            boardRequest.setRejection_reason(request.getRejection_reason());
        }
        boardRequestRepository.save(boardRequest);

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
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + auth.getName()));
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
    @Transactional
    public void updateMyRequest(UUID id, BoardRequestUpdateDTO request) {
        User proj_admin = authUser(new AuthenticationException("User not authenticated"));

        BoardRequest boardRequest = boardRequestRepository.findById(id)

                .orElseThrow(() -> new NotFoundException("No such board-request id exists"));
        if (!boardRequest.getRequester().equals(proj_admin)) {
            throw new AccessNotGivenException("You can only update your own requests");
        }


        if (!Status.PENDING.equals(boardRequest.getStatus())) {
            throw new RequestAlreadyProcessedException("This request has already been processed");
        }

        if (request.getBoard_request_name() != null && !request.getBoard_request_name().isBlank()) {
            boardRequest.setBoard_request_name(request.getBoard_request_name());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            boardRequest.setDescription(request.getDescription());
        }
        if (request.getBoard_request_key() != null && !request.getBoard_request_key().isBlank()) {
            boardRequest.setBoard_request_key(request.getBoard_request_key());
        }
        if (request.getJustification() != null && !request.getJustification().isBlank()) {
            boardRequest.setJustification(request.getJustification());
        }
        boardRequestRepository.save(boardRequest);
    }
    @Transactional
    public void deleteMyRequest(@PathVariable UUID id)
    {
        User proj_admin = authUser(new AuthenticationException("User not authenticated"));
        BoardRequest boardRequest = boardRequestRepository.findById(id)

                .orElseThrow(() -> new NotFoundException("No such board-request id exists"));
        if (!boardRequest.getRequester().equals(proj_admin)) {
            throw new AccessNotGivenException("You can only delete your own requests");
        }
        if (!Status.PENDING.equals(boardRequest.getStatus())) {
            throw new RequestAlreadyProcessedException("This request has already been processed");
        }
        boardRequestRepository.delete(boardRequest);
    }
    //Get specific board id request
    public BoardDetailsDTO getMyRequest(@PathVariable UUID id)
    {
        User current_user = authUser(new AuthenticationException("User not authenticated"));
        BoardRequest boardRequest = boardRequestRepository.findById(id)

                .orElseThrow(() -> new NotFoundException("No such board-request id exists"));
        if (!boardRequest.getRequester().equals(current_user)) {
            throw new AccessNotGivenException("You can only view your own requests");
        }
        BoardDetailsDTO boardDetailsDTO =BoardDetailsDTO.builder()
                .board_request_id(boardRequest.getBoard_request_id())
                .board_request_key(boardRequest.getBoard_request_key())
                .justification(boardRequest.getJustification())
                .requestedAt(boardRequest.getRequestedAt())
                .description(boardRequest.getDescription())
                .status(boardRequest.getStatus())
                .approvedAt(boardRequest.getApprovedAt())
                .reviewedBy(boardRequest.getReviewedBy())
                .rejectedAt(boardRequest.getRejectedAt())
                .rejection_reason(boardRequest.getRejection_reason())
                .build();
        return boardDetailsDTO;
    }
    public List<BoardDetailsDTO> getAllRequestsByStatus(Status status)
    {
        List<BoardRequest> boardRequestList =boardRequestRepository.findByStatus(status);
        return boardRequestList.stream()
                .map(this::mapTodetailsDTO)
                .toList();
    }
    private BoardDetailsDTO mapTodetailsDTO(BoardRequest entity) {
        BoardDetailsDTO dto = new BoardDetailsDTO();
        dto.setBoard_request_id(entity.getBoard_request_id());
        dto.setBoard_request_name(entity.getBoard_request_name());
        dto.setBoard_request_key(entity.getBoard_request_key());
        dto.setJustification(entity.getJustification());
        dto.setDescription(entity.getDescription());
        dto.setRequester(entity.getRequester());
        dto.setRequestedAt(entity.getRequestedAt());
        dto.setApprovedAt(entity.getApprovedAt());
        dto.setRejectedAt(entity.getRejectedAt());
        dto.setStatus(entity.getStatus());
        dto.setReviewedBy(entity.getReviewedBy());

        return dto;
    }

    public BoardDetailsDTO getRequestById(UUID id)
    {
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        Auth optionalAuth=authRepository.findByEmail(auth.getName())
                .orElseThrow(()->new AuthenticationException("Authentication issue"));
        User current_user=optionalAuth.getUser();
        if(current_user.getRole()!=Role.ADMIN)
        {
            throw new AccessNotGivenException("Only admin can view this");
        }
        BoardRequest boardRequest=boardRequestRepository.findById(id)
                .orElseThrow(()->new NotFoundException("No such board-request id exists"));

        return mapTodetailsDTO(boardRequest);
    }
}
