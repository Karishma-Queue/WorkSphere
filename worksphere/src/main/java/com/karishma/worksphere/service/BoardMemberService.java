package com.karishma.worksphere.service;

import com.karishma.worksphere.model.dto.request.AddBoardMemberDTO;
import com.karishma.worksphere.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardMemberService {
    private AuthRepository authRepository;
    public ResponseEntity<?> addBoardMember(UUID id, AddBoardMemberDTO request)
    {
        authRepository.findByEmail(request.)
    }
}
