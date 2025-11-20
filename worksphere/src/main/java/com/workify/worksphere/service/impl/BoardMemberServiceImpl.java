package com.workify.worksphere.service.impl;

import com.workify.worksphere.exception.AuthenticationException;
import com.workify.worksphere.exception.NotFoundException;
import com.workify.worksphere.exception.RequestAlreadyProcessedException;
import com.workify.worksphere.model.dto.request.AddBoardMemberDTO;
import com.workify.worksphere.model.dto.response.AddBoardMemberResponseDTO;
import com.workify.worksphere.model.dto.response.AllBoardMemberDTO;
import com.workify.worksphere.model.dto.response.BoardMemberDetailsDTO;
import com.workify.worksphere.model.entity.Auth;
import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.entity.BoardMember;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.BoardMemberId;
import com.workify.worksphere.model.value.Email;
import com.workify.worksphere.repository.AuthRepository;
import com.workify.worksphere.repository.BoardMemberRepository;
import com.workify.worksphere.repository.BoardRepository;
import com.workify.worksphere.service.BoardMemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardMemberServiceImpl implements BoardMemberService {
  private final AuthRepository authRepository;
  private final BoardRepository boardRepository;
  private final BoardMemberRepository boardMemberRepository;
  @Override
  public AddBoardMemberResponseDTO addBoardMember(String boardId, AddBoardMemberDTO request) {
   BoardId boardId1=BoardId.of(boardId);
    Board board = boardRepository.findById(boardId1)
        .orElseThrow(() ->
            new NotFoundException("No board exists with ID " + boardId));
    Email newEmail= Email.of(request.getEmail());
    Auth auth = authRepository.findByEmail(newEmail)
        .orElseThrow(() ->
            new AuthenticationException("User not authenticated"));

    User member = auth.getUser();

    if (boardMemberRepository.existsByBoardAndUser(board, member)) {
      throw new RequestAlreadyProcessedException("User is already a member of this board");
    }

    BoardMember boardMember = BoardMember.builder()
        .board(board)
        .boardMemberId(BoardMemberId.generate())
        .user(member)
        .build();

    boardMemberRepository.save(boardMember);

    return AddBoardMemberResponseDTO.builder()
        .board_member_id(boardMember.getBoardMemberId().getValue())
        .user(boardMember.getUser())
        .board(boardMember.getBoard())
        .boardRole(boardMember.getBoardRole())
        .joinedAt(boardMember.getJoinedAt())
        .build();
  }

 @Override
  public ResponseEntity<String> removeBoardMember(String boardId, String memberId) {
  BoardMemberId boardMemberId=BoardMemberId.of(memberId);
  BoardId boardId1=BoardId.of(boardId);
    BoardMember boardMember = boardMemberRepository.findById(boardMemberId)
        .orElseThrow(() ->
            new NotFoundException("No member exists with ID " + memberId));

   if (!boardMember.getBoard().getBoardId().getValue().equals(boardId)) {
     throw new NotFoundException("This member does not belong to the specified board");
   }

   boardMemberRepository.delete(boardMember);

    return ResponseEntity.ok("Member deleted successfully");
  }

  public BoardMemberDetailsDTO getMemberDetails(String boardId, String memberId) {
  BoardMemberId boardMemberId=BoardMemberId.of(memberId);
  BoardId boardId1=BoardId.of(boardId);
    BoardMember boardMember = boardMemberRepository.findById(boardMemberId)
        .orElseThrow(() ->
            new NotFoundException("No such board member exists"));

    if (!boardMember.getBoard().getBoardId().getValue().equals(boardId)) {
      throw new NotFoundException("This member does not belong to the specified board");
    }

    Auth auth = authRepository.findByUser(boardMember.getUser())
        .orElseThrow(() -> new AuthenticationException("Member not authenticated"));

    return BoardMemberDetailsDTO.builder()
        .board_id(boardMember.getBoard().getBoardId().getValue())
        .board_key(boardMember.getBoard().getBoardKey())
        .board_name(boardMember.getBoard().getBoardName())

        .user_id(boardMember.getUser().getUserId().getValue())
        .user_name(boardMember.getUser().getUserName())
        .job_title(boardMember.getUser().getJobTitle())
        .department(boardMember.getUser().getDepartment())
        .profile_picture_url(boardMember.getUser().getProfilePictureUrl())

        .boardRole(boardMember.getBoardRole())
        .email(auth.getEmail().getEmail())
        .joinedAt(boardMember.getJoinedAt())
        .build();
  }

  @Override

  public List<AllBoardMemberDTO> getBoardMembers(String boardId) {
    BoardId boardId1=BoardId.of(boardId);
    List<BoardMember> members =
        boardMemberRepository.findByBoard_BoardId(boardId1);

    return members.stream()
        .map(member -> AllBoardMemberDTO.builder()
            .user_id(member.getUser().getUserId().getValue())
            .user_name(member.getUser().getUserName())
            .boardRole(member.getBoardRole())
            .joinedAt(member.getJoinedAt())
            .build())
        .toList();
  }
}
