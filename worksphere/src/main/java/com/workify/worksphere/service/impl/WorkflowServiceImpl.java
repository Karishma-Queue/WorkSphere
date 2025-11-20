package com.workify.worksphere.service.impl;

import com.workify.worksphere.exception.AuthenticationException;
import com.workify.worksphere.exception.BadRequestException;
import com.workify.worksphere.exception.NotFoundException;
import com.workify.worksphere.model.dto.request.AddStatusDTO;
import com.workify.worksphere.model.dto.request.TransitionRequest;
import com.workify.worksphere.model.dto.request.WorkflowRequestDTO;
import com.workify.worksphere.model.dto.request.WorkflowUpdateDTO;
import com.workify.worksphere.model.dto.response.BoardWorkflowDTO;
import com.workify.worksphere.model.dto.response.StatusResponse;
import com.workify.worksphere.model.dto.response.TransitionResponse;
import com.workify.worksphere.model.dto.response.WorkflowResponse;
import com.workify.worksphere.model.dto.response.WorkflowUpdateResponse;
import com.workify.worksphere.model.entity.Auth;
import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.entity.Workflow;
import com.workify.worksphere.model.entity.WorkflowStatus;
import com.workify.worksphere.model.entity.WorkflowTransition;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.Email;
import com.workify.worksphere.model.value.WorkflowId;
import com.workify.worksphere.model.value.WorkflowStatusId;
import com.workify.worksphere.model.value.WorkflowTransitionId;
import com.workify.worksphere.repository.AuthRepository;
import com.workify.worksphere.repository.BoardRepository;
import com.workify.worksphere.repository.WorkflowRepository;
import com.workify.worksphere.repository.WorkflowStatusRepository;
import com.workify.worksphere.repository.WorkflowTransitionRepository;
import com.workify.worksphere.service.WorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkflowServiceImpl implements WorkflowService {
  private final WorkflowRepository workflowRepository;
  private final AuthRepository authRepository;
  private final WorkflowTransitionRepository workflowTransitionRepository;
  private final WorkflowStatusRepository workflowStatusRepository;
  private final BoardRepository boardRepository;
  @Override
  public WorkflowResponse createWorkflow( String boardId, WorkflowRequestDTO request)
  {
    BoardId boardId1=BoardId.of(boardId);
    Board board=boardRepository.findByBoardId(boardId1)
        .orElseThrow(()->new NotFoundException("Board not found"));
    Authentication auth= SecurityContextHolder.getContext().getAuthentication();
    Email email=Email.of(auth.getName());
    Auth optionalAuth=  authRepository.findByEmail(email)
        .orElseThrow(()->new AuthenticationException("user not authenticated"));
    User user=optionalAuth.getUser();
    Workflow workFlow=Workflow.builder()
        .createdBy(user)
        .workflowId(WorkflowId.generate())
        .workflowName(request.getWorkflow_name())
        .issueType(request.getIssue())
        .board(board)
        .build();

    workflowRepository.save(workFlow);
    WorkflowResponse response=WorkflowResponse.builder()
        .createdAt(workFlow.getCreatedAt())
        .issue(workFlow.getIssueType())
        .workflow_name(workFlow.getWorkflowName())
        .id(workFlow.getWorkflowId().getValue())
        .build();
    return response;
  }
  @Override
  public List<BoardWorkflowDTO> getBoardWorkflows(String boardId)
  {
    BoardId boardId1=BoardId.of(boardId);

    Board board=boardRepository.findByBoardId(boardId1)
        .orElseThrow(()->new NotFoundException("Board not found"));
    List<Workflow>workflows=workflowRepository.findByBoard_BoardId(boardId1);
    return workflows.stream()
        .map(w -> BoardWorkflowDTO.builder()
            .workflow_name(w.getWorkflowName())
            .issue(w.getIssueType())
            .createdAt(w.getCreatedAt())
            .build()
        )
        .toList();



  }
  @Override
  public WorkflowUpdateResponse updateWorkflow(String workflowId, WorkflowUpdateDTO request) {

    if (request.getWorkflow_name() == null && request.getIsDefault() == null) {
      throw new BadRequestException("At least one field must be provided for update");
    }
  WorkflowId workflowId1=WorkflowId.of(workflowId);
    Workflow workflow = workflowRepository.findByWorkflowId(workflowId1)
        .orElseThrow(() -> new NotFoundException("No such workflow exists"));

    if (request.getWorkflow_name() != null &&
        !request.getWorkflow_name().equals(workflow.getWorkflowName())) {

      if (workflowRepository.existsByBoard_BoardIdAndWorkflowName(
          workflow.getBoard().getBoardId(), request.getWorkflow_name())) {
        throw new BadRequestException("Workflow name already exists in this board");
      }

      workflow.setWorkflowName(request.getWorkflow_name());
    }

    if (request.getIsDefault() != null) {

      if (request.getIsDefault() && !workflow.isDefault()) {
        workflowRepository.findByBoard_BoardIdAndIsDefaultTrue(workflow.getBoard().getBoardId())
            .ifPresent(existingDefault -> {
              existingDefault.setDefault(false);
              workflowRepository.save(existingDefault);
            });

        workflow.setDefault(true);
      }

      else if (!request.getIsDefault() && workflow.isDefault()) {

        long totalWorkflows = workflowRepository.countByBoard_BoardId(workflow.getBoard().getBoardId());

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
  @Override
  public void deleteWorkflow(String workflowId) {
    WorkflowId workflowId1=WorkflowId.of(workflowId);
    Workflow workflow = workflowRepository.findByWorkflowId(workflowId1)
        .orElseThrow(() -> new NotFoundException("No workflow exists with this ID"));

    BoardId boardId = workflow.getBoard().getBoardId();

    long totalWorkflows = workflowRepository.countByBoard_BoardId(boardId);

    if (workflow.isDefault() && totalWorkflows <= 1) {
      throw new BadRequestException("Cannot delete the only workflow in the board");
    }

    workflowRepository.delete(workflow);
  }
  @Override
  public StatusResponse addStatus(String workflowId, AddStatusDTO request)
  {
      WorkflowId workflowId1=WorkflowId.of(workflowId);
    Workflow workflow=workflowRepository.findByWorkflowId(workflowId1)
        .orElseThrow(()->new NotFoundException("Workflow does not exist with this id"));
    if (workflowStatusRepository.existsByWorkflow_WorkflowIdAndStatusName(
        workflowId1, request.getStatus_name())) {
      throw new BadRequestException("Status name already exists in this workflow");
    }
    boolean started = request.getStarted() != null ? request.getStarted() : false;
    boolean ended = request.getEnded() != null ? request.getEnded() : false;
    boolean isInitial= request.getIs_initial()!=null? request.getIs_initial() : false;
    WorkflowStatus workflowStatus=WorkflowStatus.builder()
        .statusName(request.getStatus_name())
        .workflow(workflow)
        .started(started)
        .statusId(WorkflowStatusId.generate())
        .ended(ended)
        .isInitial(isInitial)
        .build();
    workflowStatusRepository.save(workflowStatus);
    StatusResponse statusResponse=StatusResponse.builder()
        .status_name(workflowStatus.getStatusName())
        .id(workflowStatus.getStatusId().getValue())
        .build();
    return statusResponse;

  }
  @Override
  public void deleteStatus(String workflowId,String statusId)
  {
    WorkflowId workflowId1=WorkflowId.of(workflowId);
    WorkflowStatusId workflowStatusId=WorkflowStatusId.of(statusId);
    Workflow workflow=workflowRepository.findByWorkflowId(workflowId1)
        .orElseThrow(()->new NotFoundException("Workflow does not exists"));
    WorkflowStatus workflowStatus=workflowStatusRepository.findByStatusId(workflowStatusId)
        .orElseThrow(()->new NotFoundException("No status with id "+statusId));
    if(!workflowStatusRepository.existsByWorkflow_WorkflowIdAndStatusId(workflowId1,workflowStatusId))
    {
      throw new BadRequestException("No status id with this particular workflow id");
    }
    workflowStatusRepository.delete(workflowStatus);
  }
  @Override
  public TransitionResponse addTransition(String workflowId, TransitionRequest request)
  {
    WorkflowId workflowId1=WorkflowId.of(workflowId);
    WorkflowStatusId workflowStatusId1=WorkflowStatusId.of(request.getFrom_status_id());
    WorkflowStatusId workflowStatusId2 = WorkflowStatusId.of(request.getTo_status_id());

    Workflow workflow = workflowRepository.findByWorkflowId(workflowId1)
        .orElseThrow(() -> new NotFoundException("Workflow with id " + workflowId + " does not exist"));

    WorkflowStatus fromStatus = workflowStatusRepository.findById(request.getFrom_status_id())
        .orElseThrow(() -> new NotFoundException("From status with id " + request.getFrom_status_id() + " does not exist"));

    WorkflowStatus toStatus = workflowStatusRepository.findById(request.getTo_status_id())
        .orElseThrow(() -> new NotFoundException("To status with id " + request.getTo_status_id() + " does not exist"));

    if (!fromStatus.getWorkflow().getWorkflowId().getValue().equals(workflowId)) {
      throw new BadRequestException("From status does not belong to this workflow");
    }

    if (!toStatus.getWorkflow().getWorkflowId().getValue().equals(workflowId)) {
      throw new BadRequestException("To status does not belong to this workflow");
    }

    if (workflowTransitionRepository.existsByWorkflow_WorkflowIdAndFromStatus_StatusIdAndToStatus_StatusId(
        workflowId1, workflowStatusId1, workflowStatusId2)) {
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
        .workflow_id(workflow.getWorkflowId().getValue())
        .build();

    return response;
  }
  @Override
  public void deleteTransition(String workflowId , String transitionId)
  {
    WorkflowId workflowId1=WorkflowId.of(workflowId);
    WorkflowTransitionId workflowTransitionId=WorkflowTransitionId.of(transitionId);

    Workflow workflow = workflowRepository.findByWorkflowId(workflowId1)
        .orElseThrow(()->new BadRequestException("No such workflow id exists"));
    WorkflowTransition workflowTransition = workflowTransitionRepository.findByWorkflowTransitionId(workflowTransitionId)
        .orElseThrow(()->new BadRequestException("No transition exists with transition id "+transitionId));
    if(!workflowTransition.getWorkflow().getWorkflowId().equals(workflow.getWorkflowId()))
    {
      throw new BadRequestException("No transition with id "+transitionId+" exists in the workflow with id "+workflowId);

    }
    workflowTransitionRepository.delete(workflowTransition);
  }


}
