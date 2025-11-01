package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.entity.BoardRequest;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.security.annotation.AllowOnlyAdmin;
import com.karishma.worksphere.security.annotation.AllowOnlyMember;
import com.karishma.worksphere.service.BoardRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardRequestController {
    private final BoardRequestService boardRequestService;
    private final AuthRepository authrepository;
     @AllowOnlyMember
    @PostMapping("/project-request")
    public ResponseEntity<?> createBoardRequest(@RequestBody BoardRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        return boardRequestService.createRequest(request, userEmail);

    }
    @AllowOnlyAdmin
    @GetMapping("/project-request")
    public ResponseEntity<List<BoardRequest>> getAllRequests()
    {
        List<BoardRequest> requests=boardRequestService.getAllBoardRequests();
        if(requests.isEmpty())
        {
            return ResponseEntity.noContent().build();

        }
        return ResponseEntity.ok(requests);

    }
    @AllowOnlyAdmin
    @PostMapping("/project-request/{id}/reject")
    public ResponseEntity<?> approveRequest(@PathVariable UUID id)
    {
        boardRequestService.approveRequest(id);
       return ResponseEntity.ok("Request approved for "+id);
    }




}












