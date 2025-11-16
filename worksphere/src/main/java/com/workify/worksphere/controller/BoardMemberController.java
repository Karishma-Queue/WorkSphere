package com.workify.worksphere.controller;

import com.workify.worksphere.model.dto.request.AddBoardMemberDTO;
import com.workify.worksphere.model.dto.response.AddBoardMemberResponseDTO;
import com.workify.worksphere.model.dto.response.AllBoardMemberDTO;
import com.workify.worksphere.model.dto.response.BoardMemberDetailsDTO;
import com.workify.worksphere.security.annotation.AllowOnlyProjAdmin;
import com.workify.worksphere.security.annotation.BoardIdParam;
import com.workify.worksphere.service.BoardMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/board")

public class BoardMemberController {
  private final BoardMemberService boardMemberService;
  @AllowOnlyProjAdmin
  //creating project member
    @PostMapping("/{board_id}/member")
    public AddBoardMemberResponseDTO addBoardMember(@BoardIdParam  @PathVariable String board_id, @RequestBody AddBoardMemberDTO request)
    {
       return boardMemberService.addBoardMember(board_id,request);
    }
    //deleting a project member
    @AllowOnlyProjAdmin
    @DeleteMapping("/{board_id}/member/{board_member_id}")
    public ResponseEntity<String> removeBoardMember(@BoardIdParam @PathVariable String board_id, @PathVariable String board_member_id)
    {
        return  boardMemberService.removeBoardMember(board_id,board_member_id);
    }
    //getting detail of a project member
    @AllowOnlyProjAdmin
    @GetMapping("/{board_id}/member/{board_member_id}")
    public BoardMemberDetailsDTO getMemberDetails(@BoardIdParam @PathVariable String board_id,
                                                  @PathVariable String board_member_id)
    {
        return boardMemberService.getMemberDetails(board_id,board_member_id);
    }

    @AllowOnlyProjAdmin
    //getting details of all project member of a board
    @GetMapping("/{board_id}/member")
    public ResponseEntity<List<AllBoardMemberDTO>> getBoardMembers(@BoardIdParam @PathVariable String board_id)
    {
       List<AllBoardMemberDTO> allBoardMemberDTOList= boardMemberService.getBoardMembers(board_id);
       return ResponseEntity.ok(allBoardMemberDTOList);

    }
 
}
