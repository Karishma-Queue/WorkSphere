package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.BadRequestException;
import com.karishma.worksphere.model.dto.request.CreateIssueDTO;
import com.karishma.worksphere.model.dto.response.IssueResponse;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardRepository;
import com.karishma.worksphere.repository.WorkflowRepository;
import com.karishma.worksphere.repository.WorkflowStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueService {
    private BoardRepository boardRepository;
    private AuthRepository authRepository;
    private WorkflowStatusRepository workflowStatusRepository;
    private WorkflowRepository workflowRepository;
    public IssueResponse createIssue(UUID id, CreateIssueDTO request)
    {
        Board board=boardRepository.findById(id)
                .orElseThrow(()->new BadRequestException("No board exists with this id "+id));
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
       Auth auth= authRepository.findByEmail(authentication.getName())
               .orElseThrow(()->new AuthenticationException("User not authenticated"));
       User user=auth.getUser();
       Workflow workflow=workflowRepository.findByBoard_BoardIdAndIssueType(id,request.getIssue_type());
       if(workflow==null)
       {
           workflow=workflowRepository.findByBoard_BoardIdAndIsDefaultTrue(id)
                   .orElseThrow(()->new BadRequestException("No default workflow for creating this issue"));
       }
        WorkflowStatus initialStatus = workflowStatusRepository
                .findByWorkflow_WorkflowIdAndIsInitialTrue(workflow.getWorkflowId())
                .orElseThrow(() -> new IllegalStateException("No initial status found in workflow"));




    }

}