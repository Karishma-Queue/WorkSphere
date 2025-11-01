package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.security.annotation.AllowOnlyMember;
import com.karishma.worksphere.service.BoardRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    

}












