package com.karishma.worksphere.controller;

import com.karishma.worksphere.exception.UserNotFoundException;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
@RequiredArgsConstructor
public class BoardRequestController {
    private final AuthRepository authrepository;
    public ResponseEntity<?> createBoardRequest(@RequestBody BoardRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        projectRequestService.createRequest(request, userEmail);
        return ResponseEntity.ok("Request submitted successfully");

    }
































    }
}
