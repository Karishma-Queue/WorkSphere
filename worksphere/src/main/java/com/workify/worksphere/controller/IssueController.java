package com.workify.worksphere.controller;

import com.workify.worksphere.model.dto.request.CreateIssueDTO;
import com.workify.worksphere.model.dto.request.CreateSprintDTO;
import com.workify.worksphere.model.dto.request.UpdateIssueDTO;
import com.workify.worksphere.model.dto.response.IssueResponse;
import com.workify.worksphere.model.dto.response.SprintResponse;
import com.workify.worksphere.model.entity.Issue;
import com.workify.worksphere.model.entity.Sprint;
import com.workify.worksphere.security.annotation.AllowOnlyProjAdmin;
import com.workify.worksphere.security.annotation.BoardIdParam;
import com.workify.worksphere.security.annotation.IssueIdParam;
import com.workify.worksphere.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/issues")
@RequiredArgsConstructor
public class IssueController {

  private final IssueService issueService;

  // ==================== Issue Endpoints ====================

  @AllowOnlyProjAdmin
  @PostMapping
  public ResponseEntity<IssueResponse> createIssue(
      @PathVariable @BoardIdParam String boardId,
      @RequestBody CreateIssueDTO request) {
    IssueResponse response = issueService.createIssue(boardId, request);
    return ResponseEntity.ok(response);
  }

  @AllowOnlyProjAdmin
  @GetMapping
  public ResponseEntity<List<IssueResponse>> getAllIssues(
      @PathVariable @BoardIdParam String boardId) {
    List<IssueResponse> responses = issueService.getAllIssues(boardId);
    return ResponseEntity.ok(responses);
  }

  @AllowOnlyProjAdmin
  @GetMapping("/backlog")
  public ResponseEntity<List<Issue>> getBacklogIssues(
      @PathVariable @BoardIdParam String boardId) {
    List<Issue> backlogIssues = issueService.getBacklogIssues(boardId);
    return ResponseEntity.ok(backlogIssues);
  }

  @AllowOnlyProjAdmin
  @GetMapping("/{issueId}")
  public ResponseEntity<IssueResponse> getIssueById(
      @PathVariable @BoardIdParam String boardId,
      @PathVariable @IssueIdParam String issueId) {
    IssueResponse response = issueService.getIssueById(boardId, issueId);
    return ResponseEntity.ok(response);
  }

  @AllowOnlyProjAdmin
  @PutMapping("/{issueId}")
  public ResponseEntity<IssueResponse> updateIssue(
      @PathVariable @BoardIdParam String boardId,
      @PathVariable @IssueIdParam String issueId,
      @RequestBody UpdateIssueDTO request) {
    IssueResponse response = issueService.updateIssue(boardId, issueId, request);
    return ResponseEntity.ok(response);
  }

  @AllowOnlyProjAdmin
  @DeleteMapping("/{issueId}")
  public ResponseEntity<Void> deleteIssue(
      @PathVariable @BoardIdParam String boardId,
      @PathVariable @IssueIdParam String issueId) {
    issueService.deleteIssue(boardId, issueId);
    return ResponseEntity.noContent().build();
  }

  @AllowOnlyProjAdmin
  @PutMapping("/{issueId}/status/{statusId}")
  public ResponseEntity<IssueResponse> changeStatus(
      @PathVariable @BoardIdParam String boardId,
      @PathVariable @IssueIdParam String issueId,
      @PathVariable String statusId) {
    IssueResponse response = issueService.changeIssueStatus(boardId, issueId, statusId);
    return ResponseEntity.ok(response);
  }

  @AllowOnlyProjAdmin
  @PatchMapping("/{issueId}/sprint/{sprintId}")
  public ResponseEntity<IssueResponse> moveToSprint(
      @PathVariable @BoardIdParam String boardId,
      @PathVariable @IssueIdParam String issueId,
      @PathVariable String sprintId) {
    IssueResponse response = issueService.moveToSprint(issueId, sprintId);
    return ResponseEntity.ok(response);
  }

  // ==================== Sprint Endpoints ====================

  @AllowOnlyProjAdmin
  @PostMapping("/sprints")
  public ResponseEntity<SprintResponse> createSprint(
      @PathVariable @BoardIdParam String boardId,
      @RequestBody CreateSprintDTO request) {
    SprintResponse response = issueService.createSprint(request, boardId);
    return ResponseEntity.ok(response);
  }

  @AllowOnlyProjAdmin
  @GetMapping("/sprints")
  public ResponseEntity<List<SprintResponse>> getAllSprints(
      @PathVariable @BoardIdParam String boardId) {
    List<SprintResponse> sprints = issueService.allSprint(boardId);
    return ResponseEntity.ok(sprints);
  }

  @AllowOnlyProjAdmin
  @PostMapping("/sprints/{sprintId}/start")
  public ResponseEntity<Sprint> startSprint(
      @PathVariable @BoardIdParam String boardId,
      @PathVariable String sprintId) {
    Sprint sprint = issueService.startSprint(sprintId, boardId);
    return ResponseEntity.ok(sprint);
  }

  @AllowOnlyProjAdmin
  @PostMapping("/sprints/{sprintId}/complete")
  public ResponseEntity<Sprint> completeSprint(
      @PathVariable @BoardIdParam String boardId,
      @PathVariable String sprintId) {
    Sprint sprint = issueService.completeSprint(sprintId, boardId);
    return ResponseEntity.ok(sprint);
  }
}