package com.workify.worksphere.controller;

import com.workify.worksphere.model.dto.request.AddStatusDTO;
import com.workify.worksphere.model.dto.request.TransitionRequest;
import com.workify.worksphere.model.dto.request.WorkflowRequestDTO;
import com.workify.worksphere.model.dto.request.WorkflowUpdateDTO;
import com.workify.worksphere.model.dto.response.*;
import com.workify.worksphere.security.annotation.AllowOnlyProjAdmin;
import com.workify.worksphere.security.annotation.BoardIdParam;
import com.workify.worksphere.security.annotation.WorkflowIdParam;
import com.workify.worksphere.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workflow")
public class WorkflowController
{
    private final WorkflowService workflowService;
    @AllowOnlyProjAdmin
    @PostMapping("/{board_id}")
    //createWorkflow by proj-admin
    public WorkflowResponse createWorkflow(@PathVariable @BoardIdParam  String board_id, @RequestBody WorkflowRequestDTO request)
    {
        return workflowService.createWorkflow(board_id,request);
    }
    @AllowOnlyProjAdmin
    @GetMapping("/board/{board_id}")
    //Get all workflows for a board
    public ResponseEntity<List<BoardWorkflowDTO>> getBoardWorkflows(@PathVariable @BoardIdParam String board_id)
    {
        List<BoardWorkflowDTO>response=workflowService.getBoardWorkflows(board_id);
        return ResponseEntity.ok(response);
    }
    //
    @AllowOnlyProjAdmin
    @PutMapping("/{workflow_id}")
    public ResponseEntity<WorkflowUpdateResponse> updateWorkflow(@PathVariable @WorkflowIdParam String workflow_id,
                                                           @RequestBody WorkflowUpdateDTO request)

    {
        WorkflowUpdateResponse response= workflowService.updateWorkflow(workflow_id,request);
             return ResponseEntity.ok(response);
    }
    @AllowOnlyProjAdmin
   @DeleteMapping("/{workflow_id}")
    public ResponseEntity<?> deleteWorkflow(@PathVariable @WorkflowIdParam String workflow_id)
   {
       workflowService.deleteWorkflow(workflow_id);
       return ResponseEntity.ok("Deleted successfully");
   }
   @AllowOnlyProjAdmin
   @PostMapping("/{workflow_id}/status")
    public StatusResponse addStatus(@PathVariable @WorkflowIdParam String workflow_id, @RequestBody AddStatusDTO request)
   {
       return workflowService.addStatus(workflow_id,request);
   }
   @AllowOnlyProjAdmin
   @DeleteMapping("/{workflow_id}/status/{status_id}")
    public ResponseEntity<String> deleteStatus(@PathVariable @WorkflowIdParam  String workflow_id,@PathVariable String status_id)
   {
       workflowService.deleteStatus(workflow_id,status_id);
       return ResponseEntity.ok("Deleted successfully status with id "+status_id);
   }
   @AllowOnlyProjAdmin
   @PostMapping("/{workflow_id}/transition")
    public TransitionResponse addTransition(@PathVariable  @WorkflowIdParam  String workflow_id,@RequestBody TransitionRequest request)
   {
       return workflowService.addTransition(workflow_id,request);
   }
   @AllowOnlyProjAdmin
   @DeleteMapping("/{workflow_id}/transition/{transition_id}")
    public ResponseEntity<String> removeTransition(@PathVariable @WorkflowIdParam String workflow_id,@PathVariable String transition_id)
   {
       workflowService.deleteTransition(workflow_id,transition_id);
       return ResponseEntity.ok("Deleted successfully");
   }
}


