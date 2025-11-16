package com.workify.worksphere.service;

import com.workify.worksphere.exception.*;
import com.workify.worksphere.model.dto.request.BoardRequestDTO;
import com.workify.worksphere.model.dto.request.BoardRequestUpdateDTO;
import com.workify.worksphere.model.dto.request.RejectRequestDTO;
import com.workify.worksphere.model.dto.response.BoardDetailsDTO;
import com.workify.worksphere.model.dto.response.BoardRequestResponse;
import com.workify.worksphere.model.entity.*;
import com.workify.worksphere.model.enums.BoardRole;
import com.workify.worksphere.model.enums.Role;
import com.workify.worksphere.model.enums.Status;
import com.workify.worksphere.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface BoardRequestService {

  ResponseEntity<?> createRequest(BoardRequestDTO request, String email);
  List<BoardRequest> getAllBoardRequests();
  void rejectRequest(String id, RejectRequestDTO request);
  List<BoardRequestResponse> myAllRequests();
  List<BoardRequestResponse> myRequestsByStatus(Status status);
  void updateMyRequest(String id, BoardRequestUpdateDTO request);
  void deleteMyRequest(@PathVariable String id);
  BoardDetailsDTO getMyRequest(@PathVariable String id);
  List<BoardDetailsDTO> getAllRequestsByStatus(Status status);
  BoardDetailsDTO getRequestById(String id);
}
