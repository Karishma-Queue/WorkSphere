package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AccessNotGivenException;
import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.BadRequestException;
import com.karishma.worksphere.exception.NotFoundException;
import com.karishma.worksphere.model.dto.request.AddStatusDTO;
import com.karishma.worksphere.model.dto.request.WorkflowRequestDTO;
import com.karishma.worksphere.model.dto.request.WorkflowUpdateDTO;
import com.karishma.worksphere.model.dto.response.BoardWorkflowDTO;
import com.karishma.worksphere.model.dto.response.StatusResponse;
import com.karishma.worksphere.model.dto.response.WorkflowResponse;
import com.karishma.worksphere.model.dto.response.WorkflowUpdateResponse;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardRepository;
import com.karishma.worksphere.repository.WorkflowRepository;
import com.karishma.worksphere.security.annotation.BoardIdParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final AuthRepository authRepository;
    private final BoardRepository boardRepository;
    public WorkflowResponse createWorkflow( UUID id, WorkflowRequestDTO request)
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
    public List<BoardWorkflowDTO>  getBoardWorkflows(UUID id)
    {
        Board board=boardRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Board not found"));
        List<Workflow>workflows=workflowRepository.findByBoard_BoardId(id);
        return workflows.stream()
                .map(w -> BoardWorkflowDTO.builder()
                        .workflow_name(w.getWorkflowName())
                        .issue(w.getIssueType())
                        .createdAt(w.getCreatedAt())
                        .build()
                )
                .toList();



    }
    public WorkflowUpdateResponse updateWorkflow(UUID id, WorkflowUpdateDTO request) {

        if (request.getWorkflow_name() == null && request.getIsDefault() == null) {
            throw new BadRequestException("At least one field must be provided for update");
        }

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such workflow exists"));

        if (request.getWorkflow_name() != null &&
                !request.getWorkflow_name().equals(workflow.getWorkflowName())) {

            if (workflowRepository.existsByBoardIdAndWorkflowName(
                    workflow.getBoard().getBoard_id(), request.getWorkflow_name())) {
                throw new BadRequestException("Workflow name already exists in this board");
            }

            workflow.setWorkflowName(request.getWorkflow_name());
        }

        if (request.getIsDefault() != null) {

            if (request.getIsDefault() && !workflow.isDefault()) {
                workflowRepository.findByBoardIdAndIsDefaultTrue(workflow.getBoard().getBoard_id())
                        .ifPresent(existingDefault -> {
                            existingDefault.setDefault(false);
                            workflowRepository.save(existingDefault);
                        });

                workflow.setDefault(true);
            }

            else if (!request.getIsDefault() && workflow.isDefault()) {

                long totalWorkflows = workflowRepository.countByBoard_BoardId(workflow.getBoard().getBoard_id());

                if (totalWorkflows <= 1) {
                    throw new BadRequestException(
                            "Cannot unset default. This is the only workflow in the board");
                }

                workflow.setDefault(false);
            }
        }

        workflowRepository.save(workflow);

        WorkflowUpdateResponse response=WorkflowUpdateResponse.builder()
                .workflow_name(workflow.getWorkflowName())
                .isDefault(workflow.isDefault())
                .build();
        return response;
    }
    public void deleteWorkflow(UUID id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No workflow exists with this ID"));

        UUID boardId = workflow.getBoard().getBoard_id();

        long totalWorkflows = workflowRepository.countByBoard_BoardId(boardId);

        if (workflow.isDefault() && totalWorkflows <= 1) {
            throw new BadRequestException("Cannot delete the only workflow in the board");
        }

        workflowRepository.delete(workflow);
    }
    public StatusResponse addStatus(UUID id, AddStatusDTO request)
    {
        Workflow workflow=workflowRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Workflow does not exist with this id"));
        WorkflowStatus workflowStatus=WorkflowStatus.builder()
                .statusName(request.getStatus_name())
                .workflow(workflow)
                .build();
        w

    }


}
