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
import com.karishma.worksphere.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final AuthRepository authRepository;
    private final BoardRepository boardRepository;
    public WorkflowResponse createWorkflow(UUID id, WorkflowRequestDTO request)
    {
        Board board=boardRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Board not found"));
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

      workflowRepository.save(workFlow);
      WorkflowResponse response=WorkflowResponse.builder()
              .createdAt(workFlow.getCreatedAt())
              .issue(workFlow.getIssueType())
              .workflow_name(workFlow.getWorkflowName())
              .id(workFlow.getWorkflowId())
              .build();
      return response;




    }
}
