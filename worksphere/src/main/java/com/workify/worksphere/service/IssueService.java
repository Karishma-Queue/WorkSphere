package com.workify.worksphere.service;

import com.workify.worksphere.exception.*;
import com.workify.worksphere.model.dto.request.CreateIssueDTO;
import com.workify.worksphere.model.dto.request.CreateSprintDTO;
import com.workify.worksphere.model.dto.request.UpdateIssueDTO;
import com.workify.worksphere.model.dto.response.IssueResponse;
import com.workify.worksphere.model.dto.response.SprintResponse;
import com.workify.worksphere.model.entity.*;
import com.workify.worksphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IssueService {
  IssueResponse createIssue(String boardId, CreateIssueDTO request);
  List<IssueResponse> getAllIssues(String boardId);
  IssueResponse getIssueById(String boardId, String issueId);
  IssueResponse updateIssue(String boardId, String issueId, UpdateIssueDTO request);
  void deleteIssue(String boardId, String issueId);
  IssueResponse changeIssueStatus(String boardId, String issueId, String statusId);
  public List<Issue> getBacklogIssues(String BoardId);
  IssueResponse moveToSprint(String issueId,String sprintId);
  SprintResponse createSprint(CreateSprintDTO request,String boardId);
  Sprint startSprint(String sprintId,String boardId);
  List<SprintResponse> allSprint(String boardId);
  Sprint completeSprint(String sprintId, String boardId);
}
