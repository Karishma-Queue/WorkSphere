package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.AddBoardMemberDTO;
import com.karishma.worksphere.model.dto.response.AddBoardMemberResponseDTO;
import com.karishma.worksphere.model.dto.response.BoardMemberDetailsDTO;
import com.karishma.worksphere.service.BoardMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/boards")
public class BoardMemberController {
  private final BoardMemberService boardMemberService;
    @PostMapping("/{board_id}/members")
    public AddBoardMemberResponseDTO addBoardMember(@PathVariable UUID board_id, @RequestBody AddBoardMemberDTO request)
    {
       return boardMemberService.addBoardMember(board_id,request);
    }
    @DeleteMapping("/{board_id}/members/{board_member_id}")
    public ResponseEntity<String> removeBoardMember(@PathVariable UUID board_id, @PathVariable UUID board_member_id)
    {
        return  boardMemberService.removeBoardMember(board_id,board_member_id);
    }
    @GetMapping("/{board_id}/members/{board_member_id}")
    public BoardMemberDetailsDTO getMemberDetails(@PathVariable UUID board_id,
                                                  @PathVariable UUID board_member_id)
    {
        return boardMemberService.getMemberDetails(board_id,board_member_id);
    }
    @GetMapping("/{board_id}/members")
    public getBoardMembers(@PathVariable UUID board_id)
    {
       List<Boar> boardMemberService.getBoardMembers(board_id);
    }

}
