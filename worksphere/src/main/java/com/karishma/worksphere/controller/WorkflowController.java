package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.WorkflowRequestDTO;
import com.karishma.worksphere.model.dto.request.WorkflowUpdateDTO;
import com.karishma.worksphere.model.dto.response.BoardWorkflowDTO;
import com.karishma.worksphere.model.dto.response.WorkflowResponse;
import com.karishma.worksphere.model.dto.response.WorkflowUpdateResponse;
import com.karishma.worksphere.security.annotation.AllowOnlyProjAdmin;
import com.karishma.worksphere.security.annotation.BoardIdParam;
import com.karishma.worksphere.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workflow")
public class WorkflowController {
    private final WorkflowService workflowService;
    @AllowOnlyProjAdmin
    @PostMapping("/{board_id}")
    //createWorkflow by proj-admin
    public WorkflowResponse createWorkflow(@PathVariable @BoardIdParam  UUID board_id, @RequestBody WorkflowRequestDTO request)
    {
        return workflowService.createWorkflow(board_id,request);
    }
    @AllowOnlyProjAdmin
    @GetMapping("/boards/{board_id}")
    //Get all workflows for a board
    public ResponseEntity<List<BoardWorkflowDTO>> getBoardWorkflows(@PathVariable @BoardIdParam UUID board_id)
    {
        List<BoardWorkflowDTO>response=workflowService.getBoardWorkflows(board_id);
        return ResponseEntity.ok(response);
    }
    //
    @AllowOnlyProjAdmin
    @PutMapping("/{workflow_id}")
    public ResponseEntity<WorkflowUpdateResponse> updateWorkflow(@PathVariable UUID workflow_id,
                                                           @RequestBody WorkflowUpdateDTO request)

    {
        WorkflowUpdateResponse response= workflowService.updateWorkflow(workflow_id,request);
             return ResponseEntity.ok(response);
    }

}
