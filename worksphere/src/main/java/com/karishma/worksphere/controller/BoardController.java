package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.response.BoardResponse;
import com.karishma.worksphere.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;
    //getting boards where user is project-admin
    @GetMapping("/board/proj-admin/{user_id}")
    public List<BoardResponse> getAllBoards(@PathVariable String user_id)
    {
      return boardService.getAllBoards(user_id);


    }
    //getting boards where user is project_member
    @GetMapping("/board/proj-member/{user_id}")
    public List<BoardResponse> getMemberBoards(@PathVariable String user_id)
    {
       return boardService.getMemberBoards(user_id);
    }
    //getting boards on basis of search words
    @GetMapping("/search")
    public List<BoardResponse> searchBoards(@RequestParam String query) {
        return boardService.searchBoards(query);
    }



}
