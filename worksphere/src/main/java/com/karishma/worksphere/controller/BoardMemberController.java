package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.AddBoardMemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/boards")
public class BoardMemberController {

    @PostMapping("/{board_id}/members")
    public ResponseEntity<String> addBoardMember(@PathVariable UUID id,@RequestBody AddBoardMemberDTO request)
    {
       boardMemberService.addBoardMember(id,request);
    }



}
