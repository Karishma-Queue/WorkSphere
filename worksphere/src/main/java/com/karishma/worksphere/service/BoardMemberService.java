package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.NotFoundException;
import com.karishma.worksphere.exception.RequestAlreadyProcessedException;
import com.karishma.worksphere.model.dto.request.AddBoardMemberDTO;
import com.karishma.worksphere.model.dto.response.AddBoardMemberResponseDTO;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.Board;
import com.karishma.worksphere.model.entity.BoardMember;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardMemberRepository;
import com.karishma.worksphere.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardMemberService {
    private final AuthRepository authRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    public AddBoardMemberResponseDTO addBoardMember(UUID id, AddBoardMemberDTO request)
    {
        Board board=boardRepository.findById(id)
                .orElseThrow(()->new NotFoundException("No such board exists with board id"+id));
       Auth auth= authRepository.findByEmail(request.getEmail())
               .orElseThrow(()->new AuthenticationException("User not authenticated"));
       User member=auth.getUser();
        boolean alreadyExists = boardMemberRepository.existsByBoardAndUser(board, member);
        if(alreadyExists)
        {
            throw new RequestAlreadyProcessedException("User is already a member of this board");
        }

        BoardMember boardMember=BoardMember.builder()
                .board(board)
                .user(member)
                .build();
        boardMemberRepository.save(boardMember);
       AddBoardMemberResponseDTO response=AddBoardMemberResponseDTO.builder()
               .board_member_id(boardMember.getBoard_member_id())
               .user(boardMember.getUser())
               .board(boardMember.getBoard())
               .joinedAt(boardMember.getJoinedAt())
               .boardRole(boardMember.getBoardRole())
               .build();
       return response;

    }
    public ResponseEntity<String> removeBoardMember(UUID board_id,UUID board_member_id){
       Board board= boardRepository.findById(board_id)
               .orElseThrow(()->new NotFoundException("There is no board with id "+board_id));
       BoardMember boardMember=boardMemberRepository.findById(board_member_id)
               .orElseThrow(()->new NotFoundException("There is no member with id "+board_member_id));
        if (!boardMember.getBoard().getBoard_id().equals(board_id)) {
            throw new NotFoundException("This member does not belong to the specified board");
        }


       boardMemberRepository.delete(boardMember);
       return ResponseEntity.ok("Member deleted successfully");

    }
  
}
