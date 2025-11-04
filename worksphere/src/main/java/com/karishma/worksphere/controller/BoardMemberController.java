package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.AddBoardMemberDTO;
import com.karishma.worksphere.model.dto.response.AddBoardMemberResponseDTO;
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
    public AddBoardMemberResponseDTO addBoardMember(@PathVariable UUID id, @RequestBody AddBoardMemberDTO request)
    {
       return boardMemberService.addBoardMember(id,request);
    }
    public 



}
