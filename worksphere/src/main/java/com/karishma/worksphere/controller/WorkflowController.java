package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.AddStatusDTO;
import com.karishma.worksphere.model.dto.request.TransitionRequest;
import com.karishma.worksphere.model.dto.request.WorkflowRequestDTO;
import com.karishma.worksphere.model.dto.request.WorkflowUpdateDTO;
import com.karishma.worksphere.model.dto.response.*;
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
   @DeleteMapping("/{workflow_id}")
    public ResponseEntity<?> deleteWorkflow(@PathVariable UUID workflow_id)
   {
       workflowService.deleteWorkflow(workflow_id);
       return ResponseEntity.ok("Deleted successfully");
   }
   @PostMapping("/{workflow_id}/statuses")
    public StatusResponse addStatus(@PathVariable UUID workflow_id, @RequestBody AddStatusDTO request)
   {
       return workflowService.addStatus(workflow_id,request);
   }
   @DeleteMapping("/{workflow_id}/statuses/{status_id}")
    public ResponseEntity<String> deleteStatus(@PathVariable UUID workflow_id,@PathVariable UUID status_id)
   {
       workflowService.deleteStatus(workflow_id,status_id);
       return ResponseEntity.ok("Deleted successfully status with id "+status_id);
   }
   @PostMapping("/{workflow_id}/transitions")
    public TransitionResponse addTransition(@PathVariable UUID workflow_id,@RequestBody TransitionRequest request)

   {
       return workflowService.addTransition(workflow_id,request);
   }
   @DeleteMapping("/{workflow_id}/transitions/{transition_id}")
    public ResponseEntity<String> removeTransition(@PathVariable UUID workflow_id,@PathVariable UUID transition_id)
   {
       workflowService.deleteTransition(workflow_id,transition_id);
       return ResponseEntity.ok("Deleted successfully");
   }
}
