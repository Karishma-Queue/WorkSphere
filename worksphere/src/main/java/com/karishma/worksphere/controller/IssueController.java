package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.response.IssueResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/boards")
public class IssueController {

    @PostMapping("/{board_id}/create-issue")
    public IssueResponse createIssue(@PathVariable UUID board_id, @RequestBody CreateIssueDTO request)
    {

    }

}
