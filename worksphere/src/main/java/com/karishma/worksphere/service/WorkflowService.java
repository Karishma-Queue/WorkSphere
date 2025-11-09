package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.NotFoundException;
import com.karishma.worksphere.model.dto.request.WorkflowRequestDTO;
import com.karishma.worksphere.model.dto.response.WorkflowResponse;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.Board;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.entity.Workflow;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final AuthRepository authRepository;
    private final BoardRepository boardRepository;
    public WorkflowResponse createWorkflow(UUID id, WorkflowRequestDTO request)
    {
        Board board=boardRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Bound not found"));
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        Auth optionalAuth=  authRepository.findByEmail(auth.getName())
                .orElseThrow(()->new AuthenticationException("user not authenticated"));
        User user=optionalAuth.getUser();
        Workflow workFlow=Workflow.builder()
                .createdBy(user)
                .workflowName(request.getWorkflow_name())
                .issueType(request.getIssue())
                .board(board)
                .build();
        


    }
}
