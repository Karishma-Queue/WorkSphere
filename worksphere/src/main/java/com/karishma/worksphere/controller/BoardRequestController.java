package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.dto.request.BoardRequestUpdateDTO;
import com.karishma.worksphere.model.dto.request.RejectRequestDTO;
import com.karishma.worksphere.model.dto.response.BoardDetailsDTO;
import com.karishma.worksphere.model.dto.response.BoardRequestResponse;
import com.karishma.worksphere.model.entity.BoardRequest;
import com.karishma.worksphere.model.enums.Status;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.security.annotation.AllowOnlyAdmin;
import com.karishma.worksphere.security.annotation.AllowOnlyMember;
import com.karishma.worksphere.service.BoardRequestService;
import lombok.RequiredArgsConstructor;
import org.hibernate.internal.build.AllowNonPortable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-request")
public class BoardRequestController {
    private final BoardRequestService boardRequestService;
    private final AuthRepository authrepository;
  //creating request
    @AllowOnlyMember
    @PostMapping
    public ResponseEntity<?> createBoardRequest(@RequestBody BoardRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        return boardRequestService.createRequest(request, userEmail);

    }
 //getting all requests for admin
    @AllowOnlyAdmin
    @GetMapping
    public ResponseEntity<List<BoardRequest>> getAllRequests() {
        List<BoardRequest> requests = boardRequestService.getAllBoardRequests();
        if (requests.isEmpty()) {
            return ResponseEntity.noContent().build();

        }
        return ResponseEntity.ok(requests);

    }
//approving requests by admin
    @AllowOnlyAdmin
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable UUID id) {
        boardRequestService.approveRequest(id);
        return ResponseEntity.ok("Request approved for " + id);
    }
//rejecting requests by admin
    @AllowOnlyAdmin
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable UUID id, @RequestBody RejectRequestDTO request) {
        boardRequestService.rejectRequest(id, request);
        return ResponseEntity.ok("Request rejected for " + id);
    }
//getting requests made by a particular member

    @AllowOnlyMember
    @GetMapping("/my-requests")
    public ResponseEntity<List<BoardRequestResponse>> getMyRequests(
            @RequestParam(required = false) Status status) {
        List<BoardRequestResponse> responses;
        if (status != null) {
            responses = boardRequestService.myRequestsByStatus(status);
        } else {
            responses = boardRequestService.myAllRequests();
        }

        return ResponseEntity.ok(responses);
    }

    //Updating board request( MEMBER CONTROL)
    @AllowOnlyMember
    @PutMapping("/my-requests/{id}/update")

    public ResponseEntity<String> updateMyRequest(@PathVariable UUID id, @RequestBody BoardRequestUpdateDTO request)
    {
        boardRequestService.updateMyRequest(id,request);
        return ResponseEntity.ok("Project-request updated successfully with id "+id);

    }
    //Deleting board request (MEMBER CONTROL)
    @DeleteMapping("/my-requests/{id}/delete")
    public ResponseEntity<String> deleteMyRequest(@PathVariable UUID id)
    {
        boardRequestService.deleteMyRequest(id);
        return ResponseEntity.ok("Project-request deleted successfully with id"+id);

    }
    //GET PARTICULAR PROJECT ID
    @GetMapping("/my-projects/{id}")
    public BoardDetailsDTO getMyRequest(@PathVariable UUID id)
    {
       return  boardRequestService.getMyRequest(id);

    }
}












