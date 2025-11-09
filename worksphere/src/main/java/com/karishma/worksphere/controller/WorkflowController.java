package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.WorkflowRequestDTO;
import com.karishma.worksphere.model.dto.response.WorkflowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workflow")
public class WorkflowController {
    @PostMapping("/{board_id}")
    public WorkflowResponse createWorkflow(@PathVariable UUID board_id, @RequestBody WorkflowRequestDTO request)
    {
        return workflowService.createWorkflow(board_id,request);
    }

}
