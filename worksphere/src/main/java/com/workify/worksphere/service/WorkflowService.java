package com.workify.worksphere.service;

import com.workify.worksphere.exception.AuthenticationException;
import com.workify.worksphere.exception.BadRequestException;
import com.workify.worksphere.exception.NotFoundException;
import com.workify.worksphere.model.dto.request.AddStatusDTO;
import com.workify.worksphere.model.dto.request.TransitionRequest;
import com.workify.worksphere.model.dto.request.WorkflowRequestDTO;
import com.workify.worksphere.model.dto.request.WorkflowUpdateDTO;
import com.workify.worksphere.model.dto.response.*;
import com.workify.worksphere.model.entity.*;
import com.workify.worksphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


public interface WorkflowService {
  WorkflowResponse createWorkflow( String id, WorkflowRequestDTO request);
  List<BoardWorkflowDTO> getBoardWorkflows(String id);
  WorkflowUpdateResponse updateWorkflow(String id, WorkflowUpdateDTO request);
  void deleteWorkflow(String id);
  StatusResponse addStatus(String id, AddStatusDTO request);
  void deleteStatus(String workflow_id,String status_id);
  TransitionResponse addTransition(String id, TransitionRequest request);
  void deleteTransition(String workflow_id , String transition_id);

}
