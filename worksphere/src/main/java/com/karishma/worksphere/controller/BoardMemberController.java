package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.AddBoardMemberDTO;
import com.karishma.worksphere.model.dto.response.AddBoardMemberResponseDTO;
import com.karishma.worksphere.model.dto.response.AllBoardMemberDTO;
import com.karishma.worksphere.model.dto.response.BoardMemberDetailsDTO;
import com.karishma.worksphere.security.annotation.AllowOnlyProjAdmin;
import com.karishma.worksphere.security.annotation.BoardIdParam;
import com.karishma.worksphere.service.BoardMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/boards")

public class BoardMemberController {
  private final BoardMemberService boardMemberService;
  @AllowOnlyProjAdmin
  //creating project member
    @PostMapping("/{board_id}/members")
    public AddBoardMemberResponseDTO addBoardMember(@BoardIdParam  @PathVariable UUID board_id, @RequestBody AddBoardMemberDTO request)
    {
       return boardMemberService.addBoardMember(board_id,request);
    }
    //deleting a project member
    @AllowOnlyProjAdmin
    @DeleteMapping("/{board_id}/members/{board_member_id}")
    public ResponseEntity<String> removeBoardMember(@BoardIdParam @PathVariable UUID board_id, @PathVariable UUID board_member_id)
    {
        return  boardMemberService.removeBoardMember(board_id,board_member_id);
    }
    //getting detail of a project member
    @AllowOnlyProjAdmin
    @GetMapping("/{board_id}/members/{board_member_id}")
    public BoardMemberDetailsDTO getMemberDetails(@BoardIdParam @PathVariable UUID board_id,
                                                  @PathVariable UUID board_member_id)
    {
        return boardMemberService.getMemberDetails(board_id,board_member_id);
    }
    @AllowOnlyProjAdmin
    //getting details of all project member of a board
    @GetMapping("/{board_id}/members")
    public ResponseEntity<List<AllBoardMemberDTO>> getBoardMembers(@BoardIdParam @PathVariable UUID board_id)
    {
       List<AllBoardMemberDTO> allBoardMemberDTOList= boardMemberService.getBoardMembers(board_id);
       return ResponseEntity.ok(allBoardMemberDTOList);

    }

}
