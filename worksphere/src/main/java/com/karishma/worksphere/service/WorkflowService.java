package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AccessNotGivenException;
import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.BadRequestException;
import com.karishma.worksphere.exception.NotFoundException;
import com.karishma.worksphere.model.dto.request.AddStatusDTO;
import com.karishma.worksphere.model.dto.request.TransitionRequest;
import com.karishma.worksphere.model.dto.request.WorkflowRequestDTO;
import com.karishma.worksphere.model.dto.request.WorkflowUpdateDTO;
import com.karishma.worksphere.model.dto.response.*;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.repository.*;
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
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final WorkflowStatusRepository workflowStatusRepository;
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
        if (workflowStatusRepository.existsByWorkflow_WorkflowIdAndStatusName(
                id, request.getStatus_name())) {
            throw new BadRequestException("Status name already exists in this workflow");
        }

        WorkflowStatus workflowStatus=WorkflowStatus.builder()
                .statusName(request.getStatus_name())
                .workflow(workflow)
                .build();
        workflowStatusRepository.save(workflowStatus);
        StatusResponse statusResponse=StatusResponse.builder()
                .status_name(workflowStatus.getStatusName())
                .id(workflowStatus.getStatus_id())
                .build();
        return statusResponse;

    }
 public void deleteStatus(UUID workflow_id,UUID status_id)
 {
     Workflow workflow=workflowRepository.findById(workflow_id)
             .orElseThrow(()->new NotFoundException("Workflow does not exists"));
     WorkflowStatus workflowStatus=workflowStatusRepository.findById(status_id)
             .orElseThrow(()->new NotFoundException("No status with id "+status_id));
     if(!workflowStatusRepository.existsByWorkflow_WorkflowIdAndStatusId(workflow_id,status_id))
     {
         throw new BadRequestException("No status id with this particular workflow id");
     }
     workflowStatusRepository.delete(workflowStatus);
 }
    public TransitionResponse addTransition(UUID id, TransitionRequest request)
    {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Workflow with id " + id + " does not exist"));

        WorkflowStatus fromStatus = workflowStatusRepository.findById(request.getFrom_status_id())
                .orElseThrow(() -> new NotFoundException("From status with id " + request.getFrom_status_id() + " does not exist"));

        WorkflowStatus toStatus = workflowStatusRepository.findById(request.getTo_status_id())
                .orElseThrow(() -> new NotFoundException("To status with id " + request.getTo_status_id() + " does not exist"));

        if (!fromStatus.getWorkflow().getWorkflowId().equals(id)) {
            throw new BadRequestException("From status does not belong to this workflow");
        }

        if (!toStatus.getWorkflow().getWorkflowId().equals(id)) {
            throw new BadRequestException("To status does not belong to this workflow");
        }

        if (workflowTransitionRepository.existsByWorkflow_WorkflowIdAndFromStatus_StatusIdAndToStatus_StatusId(
                id, request.getFrom_status_id(), request.getTo_status_id())) {
            throw new BadRequestException("Transition already exists between these statuses");
        }

        WorkflowTransition workflowTransition = WorkflowTransition.builder()
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .allowedRoles(request.getAllowedRoles())
                .workflow(workflow)
                .build();

        workflowTransitionRepository.save(workflowTransition);

        TransitionResponse response = TransitionResponse.builder()
                .from_status(fromStatus.getStatusName())
                .to_status(toStatus.getStatusName())
                .workflow_id(workflow.getWorkflowId())
                .build();

        return response;
    }


}
