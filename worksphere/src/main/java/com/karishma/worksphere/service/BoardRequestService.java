package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.BoardRequestException;
import com.karishma.worksphere.exception.MemberOnlyException;
import com.karishma.worksphere.exception.UserNotFoundException;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.BoardRequest;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardRequestRepository;
import com.karishma.worksphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardRequestService {

    private final AuthRepository authRepository;
    private final BoardRequestRepository boardRequestRepository;

    public ResponseEntity<?> createRequest(BoardRequestDTO request, String email) {

        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Authentication not found"));

        BoardRequest boardRequest = BoardRequest.builder()
                .board_name(request.getBoard_name())
                .board_key(request.getBoard_key())
                .requester(auth.getUser())
                .description(request.getDescription())
                .justification(request.getJustification())
                .build();

        boardRequestRepository.save(boardRequest);

        return ResponseEntity
                .status(201)
                .body(Map.of(
                        "message", "Board created successfully",
                        "boardName", boardRequest.getBoard_name(),
                        "boardId",boardRequest.getBoard_request_id(),
                        "requestedBy", auth.getEmail()
                ));
    }
    public List<BoardRequest> getAllBoardRequests(){
        return boardRequestRepository.findAll();
    }
    public void approveRequest(@PathVariable UUID id)
    {
        BoardRequest boardRequest=boardRequestRepository.findById(id)
                .orElseThrow(() -> new BoardRequestException("Board request not found with id: " +id));
        if(!boardRequest.getStatus().equals("PENDING"))
        {
            throw new BoardRequestException("This request already has already been "+boardRequest.getStatus());
        }
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || !auth.isAuthenticated())
        {
            throw new AuthenticationException("User not authenticated");

        }
        if(!authRepository.findByEmail(auth.getName()).isPresent())
        {
            throw new RuntimeException("No entry found");

        }
        Optional<Auth> adminauth=authRepository.findByEmail(auth.getName());
        Auth authAdmin=adminauth.get();
        

    }
}
