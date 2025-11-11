package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.CreateIssueDTO;
import com.karishma.worksphere.model.dto.response.IssueResponse;
import com.karishma.worksphere.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class IssueController {
   private final IssueService issueService;
    @PostMapping("/{board_id}/create-issue")
    public IssueResponse createIssue(@PathVariable UUID board_id, @RequestBody CreateIssueDTO request)
    {
      return issueService.createIssue(board_id,request);
    }

}
