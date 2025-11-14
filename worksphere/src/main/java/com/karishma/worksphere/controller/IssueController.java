package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.CreateIssueDTO;
import com.karishma.worksphere.model.dto.request.UpdateIssueDTO;
import com.karishma.worksphere.model.dto.response.IssueResponse;
import com.karishma.worksphere.security.annotation.AllowOnlyProjAdmin;
import com.karishma.worksphere.security.annotation.BoardIdParam;
import com.karishma.worksphere.security.annotation.IssueIdParam;
import com.karishma.worksphere.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/boards/{boardId}/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @AllowOnlyProjAdmin
    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @PathVariable @BoardIdParam UUID boardId,
            @RequestBody CreateIssueDTO request) {
        IssueResponse response = issueService.createIssue(boardId, request);
        return ResponseEntity.ok(response);
    }

    @AllowOnlyProjAdmin
    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAllIssues(
            @PathVariable @BoardIdParam UUID boardId) {
        List<IssueResponse> responses = issueService.getAllIssues(boardId);
        return ResponseEntity.ok(responses);
    }

    @AllowOnlyProjAdmin
    @GetMapping("/{issueId}")
    public ResponseEntity<IssueResponse> getIssueById(
            @PathVariable UUID boardId,
            @PathVariable @IssueIdParam UUID issueId) {
        IssueResponse response = issueService.getIssueById(boardId, issueId);
        return ResponseEntity.ok(response);
    }

    @AllowOnlyProjAdmin
    @PutMapping("/{issueId}")
    public ResponseEntity<IssueResponse> updateIssue(
            @PathVariable UUID boardId,
            @PathVariable @IssueIdParam UUID issueId,
            @RequestBody UpdateIssueDTO request) {
        IssueResponse response = issueService.updateIssue(boardId, issueId, request);
        return ResponseEntity.ok(response);
    }

    @AllowOnlyProjAdmin
    @DeleteMapping("/{issueId}")
    public ResponseEntity<Void> deleteIssue(
            @PathVariable UUID boardId,
            @PathVariable @IssueIdParam UUID issueId) {
        issueService.deleteIssue(boardId, issueId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{issueId}/status/{statusId}")
    public ResponseEntity<IssueResponse> changeStatus(
            @PathVariable UUID boardId,
            @PathVariable @IssueIdParam UUID issueId,
            @PathVariable UUID statusId) {
        IssueResponse response = issueService.changeIssueStatus(boardId, issueId, statusId);
        return ResponseEntity.ok(response);
    }
}