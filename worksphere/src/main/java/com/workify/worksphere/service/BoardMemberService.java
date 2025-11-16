package com.workify.worksphere.service;

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
import com.workify.worksphere.repository.AuthRepository;
import com.workify.worksphere.repository.BoardMemberRepository;
import com.workify.worksphere.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardMemberService {

    private final AuthRepository authRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    public AddBoardMemberResponseDTO addBoardMember(String boardId, AddBoardMemberDTO request) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() ->
                        new NotFoundException("No board exists with ID " + boardId));

        Auth auth = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AuthenticationException("User not authenticated"));

        User member = auth.getUser();

        if (boardMemberRepository.existsByBoardAndUser(board, member)) {
            throw new RequestAlreadyProcessedException("User is already a member of this board");
        }

        BoardMember boardMember = BoardMember.builder()
                .board(board)
                .user(member)
                .build();

        boardMemberRepository.save(boardMember);

        return AddBoardMemberResponseDTO.builder()
                .board_member_id(boardMember.getBoardMemberId())
                .user(boardMember.getUser())
                .board(boardMember.getBoard())
                .boardRole(boardMember.getBoardRole())
                .joinedAt(boardMember.getJoinedAt())
                .build();
    }


    public ResponseEntity<String> removeBoardMember(String boardId, String memberId) {

        BoardMember boardMember = boardMemberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("No member exists with ID " + memberId));

        if (!boardMember.getBoard().getBoardId().equals(boardId)) {
            throw new NotFoundException("This member does not belong to the specified board");
        }

        boardMemberRepository.delete(boardMember);

        return ResponseEntity.ok("Member deleted successfully");
    }

    public BoardMemberDetailsDTO getMemberDetails(String boardId, String memberId) {

        BoardMember boardMember = boardMemberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("No such board member exists"));

        if (!boardMember.getBoard().getBoardId().equals(boardId)) {
            throw new NotFoundException("This member does not belong to the specified board");
        }

        Auth auth = authRepository.findByUser(boardMember.getUser())
                .orElseThrow(() -> new AuthenticationException("Member not authenticated"));

        return BoardMemberDetailsDTO.builder()
                .board_id(boardMember.getBoard().getBoardId())
                .board_key(boardMember.getBoard().getBoardKey())
                .board_name(boardMember.getBoard().getBoardName())

                .user_id(boardMember.getUser().getUserId())
                .user_name(boardMember.getUser().getUserName())
                .job_title(boardMember.getUser().getJobTitle())
                .department(boardMember.getUser().getDepartment())
                .profile_picture_url(boardMember.getUser().getProfilePictureUrl())

                .boardRole(boardMember.getBoardRole())
                .email(auth.getEmail())
                .joinedAt(boardMember.getJoinedAt())
                .build();
    }


    public List<AllBoardMemberDTO> getBoardMembers(String boardId) {

        List<BoardMember> members =
                boardMemberRepository.findByBoard_BoardId(boardId);

        return members.stream()
                .map(member -> AllBoardMemberDTO.builder()
                        .user_id(member.getUser().getUserId())
                        .user_name(member.getUser().getUserName())
                        .boardRole(member.getBoardRole())
                        .joinedAt(member.getJoinedAt())
                        .build())
                .toList();
    }
}
