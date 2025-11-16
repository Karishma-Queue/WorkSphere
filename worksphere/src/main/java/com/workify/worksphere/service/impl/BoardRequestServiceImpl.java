package com.workify.worksphere.service.impl;

import com.workify.worksphere.exception.AccessNotGivenException;
import com.workify.worksphere.exception.AuthenticationException;
import com.workify.worksphere.exception.BoardRequestException;
import com.workify.worksphere.exception.NotFoundException;
import com.workify.worksphere.exception.RequestAlreadyProcessedException;
import com.workify.worksphere.exception.UserNotFoundException;
import com.workify.worksphere.model.dto.request.BoardRequestDTO;
import com.workify.worksphere.model.dto.request.BoardRequestUpdateDTO;
import com.workify.worksphere.model.dto.request.RejectRequestDTO;
import com.workify.worksphere.model.dto.response.BoardDetailsDTO;
import com.workify.worksphere.model.dto.response.BoardRequestResponse;
import com.workify.worksphere.model.entity.Auth;
import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.entity.BoardMember;
import com.workify.worksphere.model.entity.BoardRequest;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.enums.BoardRole;
import com.workify.worksphere.model.enums.Role;
import com.workify.worksphere.model.enums.Status;
import com.workify.worksphere.repository.AuthRepository;
import com.workify.worksphere.repository.BoardMemberRepository;
import com.workify.worksphere.repository.BoardRepository;
import com.workify.worksphere.repository.BoardRequestRepository;
import com.workify.worksphere.service.BoardRequestService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class BoardRequestServiceImpl implements BoardRequestService {
  private final AuthRepository authRepository;
  private final BoardRequestRepository boardRequestRepository;
  private final BoardMemberRepository boardMemberRepository;
  private final BoardRepository boardRepository;
@Override
  public ResponseEntity<?> createRequest(BoardRequestDTO request, String email) {

    Auth auth = authRepository.findByEmail(email)
        .orElseThrow(() -> new AuthenticationException("Authentication not found"));

    BoardRequest boardRequest = BoardRequest.builder()
        .boardRequestName(request.getBoard_request_name())
        .boardRequestKey(request.getBoard_request_key())
        .requester(auth.getUser())
        .description(request.getDescription())
        .justification(request.getJustification())
        .build();
    System.out.println("In board request requester is "+boardRequest.getRequester().getUserName());
    boardRequestRepository.save(boardRequest);

    return ResponseEntity
        .status(201)
        .body(Map.of(
            "message", "Board created successfully",
            "boardName", boardRequest.getBoardRequestName(),
            "boardId", boardRequest.getBoardRequestId(),
            "requestedBy", auth.getEmail()
        ));
  }
@Override
  public List<BoardRequest> getAllBoardRequests() {
    return boardRequestRepository.findAll();
  }


  public void approveRequest( String id) {
    BoardRequest boardRequest = boardRequestRepository.findById(id)
        .orElseThrow(() -> new BoardRequestException("Board request not found with id: " + id));

    if (!Status.PENDING.equals(boardRequest.getStatus())) {
      throw new BoardRequestException("This request has already been " + boardRequest.getStatus());
    }

    User admin = authUser(new AuthenticationException("User not authenticated") {
    });
    System.out.println("=== APPROVE REQUEST DEBUG ===");
    System.out.println("Requester: " + boardRequest.getRequester());
    System.out.println("Requester userId: " + boardRequest.getRequester().getUserId());
    System.out.println("Requester userName: " + boardRequest.getRequester().getUserName());

    Board board = Board.builder()
        .boardName(boardRequest.getBoardRequestName())
        .boardKey(boardRequest.getBoardRequestKey())
        .description(boardRequest.getDescription())
        .createdBy(boardRequest.getRequester())
        .build();
    boardRepository.save(board);


    BoardMember boardMember = BoardMember.builder()
        .board(board)
        .user(board.getCreatedBy())
        .boardRole(BoardRole.PROJECT_ADMIN)
        .build();

    try {
      BoardMember saved = boardMemberRepository.save(boardMember);
      System.out.println("BoardMember SAVED successfully! ID: " + saved.getBoardMemberId());
    } catch (Exception e) {
      System.out.println(" ERROR saving BoardMember: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }


    boardRequest.setStatus(Status.APPROVED);
    boardRequest.setApprovedAt(LocalDateTime.now());
    boardRequest.setReviewedBy(admin);
    System.out.println("Reviewed by "+admin.getUserName());
    boardRequestRepository.save(boardRequest);
  }
@Override
  public void rejectRequest(String id, RejectRequestDTO request) {
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
      boardRequest.setRejectionReason(request.getRejection_reason());
    }
    boardRequestRepository.save(boardRequest);
    System.out.println("=== APPROVE REQUEST COMPLETED ===");


  }
@Override
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
    dto.setId(entity.getBoardRequestId());
    dto.setName(entity.getBoardRequestName());
    dto.setRequesterName(entity.getRequester());
    return dto;
  }
  @Override
  public List<BoardRequestResponse> myRequestsByStatus(Status status)
  {
    User member = authUser(new AuthenticationException("User not authenticated"));
    List<BoardRequest> boardRequestList= boardRequestRepository.findByRequesterAndStatus(member,status);
    return boardRequestList.stream()
        .map(this::mapToDTO)
        .toList();

  }
  @Override
  public void updateMyRequest(String id, BoardRequestUpdateDTO request) {
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
      boardRequest.setBoardRequestName(request.getBoard_request_name());
    }
    if (request.getDescription() != null && !request.getDescription().isBlank()) {
      boardRequest.setDescription(request.getDescription());
    }
    if (request.getBoard_request_key() != null && !request.getBoard_request_key().isBlank()) {
      boardRequest.setBoardRequestKey(request.getBoard_request_key());
    }
    if (request.getJustification() != null && !request.getJustification().isBlank()) {
      boardRequest.setJustification(request.getJustification());
    }
    boardRequestRepository.save(boardRequest);
  }
  @Override
  public void deleteMyRequest(@PathVariable String id)
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
  @Override
  public BoardDetailsDTO getMyRequest(@PathVariable String id)
  {
    User current_user = authUser(new AuthenticationException("User not authenticated"));
    BoardRequest boardRequest = boardRequestRepository.findById(id)

        .orElseThrow(() -> new NotFoundException("No such board-request id exists"));
    if (!boardRequest.getRequester().equals(current_user)) {
      throw new AccessNotGivenException("You can only view your own requests");
    }
    BoardDetailsDTO boardDetailsDTO =BoardDetailsDTO.builder()
        .board_request_id(boardRequest.getBoardRequestId())
        .board_request_key(boardRequest.getBoardRequestKey())
        .justification(boardRequest.getJustification())
        .requestedAt(boardRequest.getRequestedAt())
        .board_request_name(boardRequest.getBoardRequestName())
        .description(boardRequest.getDescription())
        .status(boardRequest.getStatus())
        .approvedAt(boardRequest.getApprovedAt())
        .reviewedBy(boardRequest.getReviewedBy())
        .rejectedAt(boardRequest.getRejectedAt())
        .rejection_reason(boardRequest.getRejectionReason())
        .build();
    return boardDetailsDTO;
  }
  @Override
  public List<BoardDetailsDTO> getAllRequestsByStatus(Status status)
  {
    List<BoardRequest> boardRequestList =boardRequestRepository.findByStatus(status);
    return boardRequestList.stream()
        .map(this::mapTodetailsDTO)
        .toList();
  }
  private BoardDetailsDTO mapTodetailsDTO(BoardRequest entity) {
    BoardDetailsDTO dto = new BoardDetailsDTO();
    dto.setBoard_request_id(entity.getBoardRequestId());
    dto.setBoard_request_name(entity.getBoardRequestName());
    dto.setBoard_request_key(entity.getBoardRequestKey());
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
@Override
  public BoardDetailsDTO getRequestById(String id)
  {
    Authentication auth=SecurityContextHolder.getContext().getAuthentication();
    Auth optionalAuth=authRepository.findByEmail(auth.getName())
        .orElseThrow(()->new AuthenticationException("Authentication issue"));
    User current_user=optionalAuth.getUser();
    if(current_user.getRole()!= Role.ADMIN)
    {
      throw new AccessNotGivenException("Only admin can view this");
    }
    BoardRequest boardRequest=boardRequestRepository.findById(id)
        .orElseThrow(()->new NotFoundException("No such board-request id exists"));

    return mapTodetailsDTO(boardRequest);
  }
}
