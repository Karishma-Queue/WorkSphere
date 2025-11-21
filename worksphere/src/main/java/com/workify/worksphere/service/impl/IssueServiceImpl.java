package com.workify.worksphere.service.impl;

import com.workify.worksphere.exception.AuthenticationException;
import com.workify.worksphere.exception.BadRequestException;
import com.workify.worksphere.exception.BoardRequestException;
import com.workify.worksphere.exception.NotFoundException;
import com.workify.worksphere.model.dto.request.CreateIssueDTO;
import com.workify.worksphere.model.dto.request.CreateSprintDTO;
import com.workify.worksphere.model.dto.request.UpdateIssueDTO;
import com.workify.worksphere.model.dto.response.IssueResponse;
import com.workify.worksphere.model.dto.response.SprintResponse;
import com.workify.worksphere.model.entity.Auth;
import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.entity.BoardMember;
import com.workify.worksphere.model.entity.Issue;
import com.workify.worksphere.model.entity.Sprint;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.entity.Workflow;
import com.workify.worksphere.model.entity.WorkflowStatus;
import com.workify.worksphere.model.enums.SprintStatus;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.BoardMemberId;
import com.workify.worksphere.model.value.Email;
import com.workify.worksphere.model.value.IssueId;
import com.workify.worksphere.model.value.SprintId;
import com.workify.worksphere.model.value.UserId;
import com.workify.worksphere.model.value.WorkflowStatusId;
import com.workify.worksphere.repository.AuthRepository;
import com.workify.worksphere.repository.BoardMemberRepository;
import com.workify.worksphere.repository.BoardRepository;
import com.workify.worksphere.repository.IssueRepository;
import com.workify.worksphere.repository.SprintRepository;
import com.workify.worksphere.repository.WorkflowRepository;
import com.workify.worksphere.repository.WorkflowStatusRepository;
import com.workify.worksphere.service.IssueService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {
  private final BoardMemberRepository boardMemberRepository;
  private final BoardRepository boardRepository;
  private final AuthRepository authRepository;
  private final WorkflowStatusRepository workflowStatusRepository;
  private final WorkflowRepository workflowRepository;
  private final IssueRepository issueRepository;
  private final SprintRepository sprintRepository;

  @Override
  public IssueResponse createIssue(String boardId, CreateIssueDTO request) {
    BoardId boardIdVO = BoardId.of(boardId);

    Board board = boardRepository
        .findByBoardId(boardIdVO)
        .orElseThrow(() -> new NotFoundException("No board exists with id " + boardId));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Email email = Email.of(authentication.getName());
    Auth auth = authRepository
        .findByEmail(email)
        .orElseThrow(() -> new AuthenticationException("User not authenticated"));
    User reporter = auth.getUser();

    Workflow workflow = workflowRepository
        .findByBoard_BoardIdAndIssueType(boardIdVO, request.getIssue_type());
    if (workflow == null) {
      workflow = workflowRepository
          .findByBoard_BoardIdAndIsDefaultTrue(boardIdVO)
          .orElseThrow(() -> new BadRequestException("No default workflow found"));
    }

    WorkflowStatus initialStatus = workflowStatusRepository
        .findByWorkflow_WorkflowIdAndIsInitialTrue(workflow.getWorkflowId())
        .orElseThrow(() -> new IllegalStateException("No initial status found"));

    User assignee = null;
    if (request.getAssignee_id() != null) {
      UserId assigneeUserId = UserId.of(request.getAssignee_id());
      BoardMember boardMember = boardMemberRepository
          .findByBoard_BoardIdAndUser_UserId(boardIdVO, assigneeUserId)
          .orElseThrow(() -> new NotFoundException("No member exists in this board with this id"));
      assignee = boardMember.getUser();
    }

    Issue parent = null;
    if (request.getParent_id() != null) {
      IssueId parentIssueId = IssueId.of(request.getParent_id());
      parent = issueRepository
          .findByIssueId(parentIssueId)
          .orElseThrow(() -> new NotFoundException("Parent issue not found"));
    }

    Issue epic = null;
    if (request.getEpic_id() != null) {
      IssueId epicIssueId = IssueId.of(request.getEpic_id());
      epic = issueRepository
          .findByIssueId(epicIssueId)
          .orElseThrow(() -> new NotFoundException("Epic issue not found"));
    }

    Issue issue = Issue.builder()
        .board(board)
        .reporter(reporter)
        .issueId(IssueId.generate())
        .workflow(workflow)
        .status(initialStatus)
        .issue(request.getIssue_type())
        .summary(request.getSummary())
        .priority(request.getPriority())
        .description(request.getDescription())
        .assignee(assignee)
        .parent(parent)
        .epic(epic)
        .dueDate(request.getDue_Date())
        .build();

    Issue savedIssue = issueRepository.save(issue);
    return mapToIssueResponse(savedIssue);
  }

  @Override
  public List<IssueResponse> getAllIssues(String boardId) {
    BoardId boardIdVO = BoardId.of(boardId);
    List<Issue> issues = issueRepository.findByBoard_BoardId(boardIdVO);
    return issues.stream().map(this::mapToIssueResponse).toList();
  }

  @Override
  public IssueResponse getIssueById(String boardId, String issueId) {
    BoardId boardIdVO = BoardId.of(boardId);
    IssueId issueIdVO = IssueId.of(issueId);

    Issue issue = issueRepository
        .findByIssueId(issueIdVO)
        .orElseThrow(() -> new NotFoundException("Issue not found"));

    if (!issue.getBoard().getBoardId().equals(boardIdVO)) {
      throw new BadRequestException("Issue does not belong to this board");
    }
    return mapToIssueResponse(issue);
  }

  @Override
  public IssueResponse updateIssue(String boardId, String issueId, UpdateIssueDTO request) {
    BoardId boardIdVO = BoardId.of(boardId);
    IssueId issueIdVO = IssueId.of(issueId);

    Issue issue = issueRepository
        .findByIssueId(issueIdVO)
        .orElseThrow(() -> new NotFoundException("Issue not found"));

    if (!issue.getBoard().getBoardId().equals(boardIdVO)) {
      throw new BadRequestException("Issue does not belong to this board");
    }

    if (request.getSummary() != null) issue.setSummary(request.getSummary());
    if (request.getDescription() != null) issue.setDescription(request.getDescription());
    if (request.getPriority() != null) issue.setPriority(request.getPriority());
    if (request.getDue_date() != null) issue.setDueDate(request.getDue_date());

    if (request.getAssignee_id() != null) {
      UserId assigneeUserId = UserId.of(request.getAssignee_id());
      BoardMember boardMember = boardMemberRepository
          .findByBoard_BoardIdAndUser_UserId(boardIdVO, assigneeUserId)
          .orElseThrow(() -> new NotFoundException("Assignee not found in this board"));
      issue.setAssignee(boardMember.getUser());
    }

    Issue updated = issueRepository.save(issue);
    return mapToIssueResponse(updated);
  }

  @Override
  public void deleteIssue(String boardId, String issueId) {
    BoardId boardIdVO = BoardId.of(boardId);
    IssueId issueIdVO = IssueId.of(issueId);

    Issue issue = issueRepository
        .findByIssueId(issueIdVO)
        .orElseThrow(() -> new NotFoundException("Issue not found"));

    if (!issue.getBoard().getBoardId().equals(boardIdVO)) {
      throw new BadRequestException("Issue does not belong to this board");
    }
    issueRepository.delete(issue);
  }

  @Override
  public IssueResponse changeIssueStatus(String boardId, String issueId, String statusId) {
    BoardId boardIdVO = BoardId.of(boardId);
    IssueId issueIdVO = IssueId.of(issueId);
    WorkflowStatusId statusIdVO = WorkflowStatusId.of(statusId);

    Issue issue = issueRepository
        .findByIssueId(issueIdVO)
        .orElseThrow(() -> new NotFoundException("Issue not found"));

    if (!issue.getBoard().getBoardId().equals(boardIdVO)) {
      throw new BadRequestException("Issue does not belong to this board");
    }

    WorkflowStatus newStatus = workflowStatusRepository
        .findByStatusId(statusIdVO)
        .orElseThrow(() -> new NotFoundException("Status not found"));

    if (!newStatus.getWorkflow().getWorkflowId().equals(issue.getWorkflow().getWorkflowId())) {
      throw new BadRequestException("Status does not belong to the same workflow");
    }

    issue.setStatus(newStatus);
    Issue updated = issueRepository.save(issue);
    return mapToIssueResponse(updated);
  }

  private IssueResponse mapToIssueResponse(Issue issue) {
    return IssueResponse.builder()
        .issueId(issue.getIssueId().getValue())
        .summary(issue.getSummary())
        .description(issue.getDescription())
        .priority(issue.getPriority().name())
        .issueType(issue.getIssue().name())
        .status(issue.getStatus().getStatusName())
        .boardName(issue.getBoard().getBoardName())
        .workflowName(issue.getWorkflow().getWorkflowName())
        .dueDate(issue.getDueDate())
        .createdAt(issue.getCreatedAt())
        .updatedAt(issue.getUpdatedAt())
        .reporterId(issue.getReporter().getUserId().getValue())
        .reporterName(issue.getReporter().getUserName())
        .assigneeId(issue.getAssignee() != null ? issue.getAssignee().getUserId().getValue() : null)
        .assigneeName(issue.getAssignee() != null ? issue.getAssignee().getUserName() : null)
        .parentId(issue.getParent() != null ? issue.getParent().getIssueId().getValue() : null)
        .epicId(issue.getEpic() != null ? issue.getEpic().getIssueId().getValue() : null)
        .build();
  }

  @Override
  public List<Issue> getBacklogIssues(String boardId) {
    BoardId boardIdVO = BoardId.of(boardId);
    List<Issue> backlogs = issueRepository.findByBoard_BoardIdAndSprintIsNull(boardIdVO);
    return backlogs;
  }

  @Override
  public IssueResponse moveToSprint(String issueId, String sprintId) {
    IssueId issueIdVO = IssueId.of(issueId);
    SprintId sprintIdVO = SprintId.of(sprintId);

    Issue issue = issueRepository
        .findByIssueId(issueIdVO)
        .orElseThrow(() -> new NotFoundException("No issue exists with id " + issueId));

    Board board = issue.getBoard();
    if (issue.getSprint() != null) {
      throw new BadRequestException("Issue already in another sprint");
    }

    Sprint sprint = sprintRepository
        .findBySprintId(sprintIdVO)
        .orElseThrow(() -> new NotFoundException("No sprint exists with this id " + sprintId));

    if (!sprint.getBoard().getBoardId().equals(board.getBoardId())) {
      throw new BadRequestException("Issue and Sprint do not belong to the same board");
    }

    if (sprint.getStatus() == SprintStatus.COMPLETED) {
      throw new BoardRequestException("Sprint has already been completed");
    }

    issue.setSprint(sprint);
    Issue saved = issueRepository.save(issue);
    return mapToIssueResponse(saved);
  }

  @Override
  public List<SprintResponse> allSprint(String boardId) {
    BoardId boardIdVO = BoardId.of(boardId);

    Board board = boardRepository
        .findByBoardId(boardIdVO)
        .orElseThrow(() -> new NotFoundException("No board exists with this id " + boardId));

    List<Sprint> sprints = sprintRepository.findByBoard_BoardId(boardIdVO);
    return sprints.stream()
        .map(sprint -> SprintResponse.builder()
            .sprintId(sprint.getSprintId().getValue())
            .springName(sprint.getSprintName())
            .startDate(sprint.getStartDate())
            .endDate(sprint.getEndDate())
            .boardId(boardIdVO.getValue())
            .sprintStatus(sprint.getStatus())
            .build())
        .toList();
  }

  @Override
  public SprintResponse createSprint(CreateSprintDTO request, String boardId) {
    // Get authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Email email = Email.of(authentication.getName());
    Auth auth = authRepository
        .findByEmail(email)
        .orElseThrow(() -> new AuthenticationException("User not authenticated"));
    User user = auth.getUser();

    // Validations
    if (request.getName() == null || request.getName().trim().isEmpty()) {
      throw new BadRequestException("Name cannot be empty");
    }
    if (request.getStartDate() == null) {
      throw new BadRequestException("Start date cannot be empty");
    }
    if (request.getEndDate() == null) {
      throw new BadRequestException("End date cannot be empty");
    }
    if (request.getEndDate().isBefore(request.getStartDate())) {
      throw new BadRequestException("End date must be after start date");
    }

    // Get board
    BoardId boardIdVO = BoardId.of(boardId);
    Board board = boardRepository
        .findByBoardId(boardIdVO)
        .orElseThrow(() -> new NotFoundException("No board exists with id " + boardId));

    // Check if sprint name already exists in this board
    boolean exists = sprintRepository.existsByBoard_BoardIdAndSprintName(boardIdVO, request.getName());
    if (exists) {
      throw new BadRequestException("Sprint with name '" + request.getName() + "' already exists in this board");
    }

    // Create sprint
    Sprint newSprint = Sprint.builder()
        .sprintName(request.getName())
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .createdBy(user)
        .sprintId(SprintId.generate())
        .board(board)
        .status(SprintStatus.PLANNED)
        .build();

    Sprint saved = sprintRepository.save(newSprint);

    // Build response
    return SprintResponse.builder()
        .sprintId(saved.getSprintId().getValue())
        .springName(saved.getSprintName())
        .startDate(saved.getStartDate())
        .endDate(saved.getEndDate())
        .boardId(saved.getBoard().getBoardId().getValue())
        .sprintStatus(saved.getStatus())
        .build();
  }

  @Override
  public Sprint completeSprint(String sprintId, String boardId) {
    BoardId boardIdVO = BoardId.of(boardId);
    SprintId sprintIdVO = SprintId.of(sprintId);

    Board board = boardRepository
        .findByBoardId(boardIdVO)
        .orElseThrow(() -> new NotFoundException("No board exists with this id " + boardId));

    Sprint sprint = sprintRepository
        .findBySprintId(sprintIdVO)
        .orElseThrow(() -> new NotFoundException("No sprint exists with this id " + sprintId));

    if (!sprint.getBoard().getBoardId().equals(boardIdVO)) {
      throw new BadRequestException("This sprint does not belong to this board");
    }

    if (sprint.getStatus() != SprintStatus.ACTIVE) {
      throw new BadRequestException("Only active sprints can be completed");
    }

    // Get all issues in sprint
    List<Issue> sprintIssues = issueRepository.findBySprint_SprintId(sprintIdVO);

    // Separate completed and incomplete issues
    List<Issue> incompleteIssues = new ArrayList<>();

    for (Issue issue : sprintIssues) {
      if (!issue.getStatus().getIsFinal()) {
        incompleteIssues.add(issue);
        issue.setSprint(null);  // Move back to backlog
      }
    }

    // Save incomplete issues
    if (!incompleteIssues.isEmpty()) {
      issueRepository.saveAll(incompleteIssues);
    }

    // Complete sprint
    sprint.setStatus(SprintStatus.COMPLETED);
    sprint.setCompletedAt(LocalDateTime.now());

    return sprintRepository.save(sprint);
  }

  @Override
  public Sprint startSprint(String sprintId, String boardId) {
    BoardId boardIdVO = BoardId.of(boardId);
    SprintId sprintIdVO = SprintId.of(sprintId);

    Board board = boardRepository
        .findByBoardId(boardIdVO)
        .orElseThrow(() -> new NotFoundException("No board exists with this id " + boardId));

    Sprint sprint = sprintRepository
        .findBySprintId(sprintIdVO)
        .orElseThrow(() -> new NotFoundException("No sprint exists with this id " + sprintId));

    if (!sprint.getBoard().getBoardId().equals(boardIdVO)) {
      throw new BadRequestException("This sprint does not belong to this board");
    }

    if (sprint.getStatus() == SprintStatus.ACTIVE) {
      throw new BadRequestException("Sprint is already active");
    }

    if (sprint.getStatus() == SprintStatus.COMPLETED) {
      throw new BadRequestException("Cannot start a completed sprint");
    }

    boolean hasActiveSprint = sprintRepository
        .existsByBoard_BoardIdAndStatus(boardIdVO, SprintStatus.ACTIVE);
    if (hasActiveSprint) {
      throw new BadRequestException("Board already has an active sprint. Complete it before starting a new one.");
    }

    List<Issue> sprintIssues = issueRepository.findBySprint_SprintId(sprintIdVO);
    if (sprintIssues.isEmpty()) {
      throw new BadRequestException("Cannot start sprint with no issues. Add issues first.");
    }

    sprint.setStatus(SprintStatus.ACTIVE);
    sprint.setStartedAt(LocalDateTime.now());
    return sprintRepository.save(sprint);
  }
}