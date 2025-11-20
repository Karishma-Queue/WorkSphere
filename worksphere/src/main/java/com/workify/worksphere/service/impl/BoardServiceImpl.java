package com.workify.worksphere.service.impl;

import com.workify.worksphere.model.dto.response.BoardResponse;
import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.entity.BoardMember;
import com.workify.worksphere.model.enums.BoardRole;
import com.workify.worksphere.model.value.UserId;
import com.workify.worksphere.repository.BoardMemberRepository;
import com.workify.worksphere.repository.BoardRepository;
import com.workify.worksphere.service.BoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
  private final BoardRepository boardRepository;
  private final BoardMemberRepository boardMemberRepository;
  @Override
  public List<BoardResponse> getAllBoards(String userId)
  {
    UserId userId1=UserId.of(userId);
    List<Board> boardList=boardRepository.findByCreatedBy_UserId(userId1);
    return boardList.stream().map(board ->
        BoardResponse.builder()
            .boardId(board.getBoardId().getValue())
            .boardName(board.getBoardName())
            .boardKey(board.getBoardKey())
            .description(board.getDescription())
            .createdAt(board.getCreatedAt())
            .createdByUserId(board.getCreatedBy().getUserId().getValue())
            .createdByUserName(board.getCreatedBy().getUserName())
            .build()
    ).toList();


  }
  @Override
  public List<BoardResponse> getMemberBoards( String userId)
  {
    UserId userId1=UserId.of(userId);
    List<BoardMember> boardMemberList =
        boardMemberRepository.findByUser_UserIdAndBoardRole(
            userId1,
            BoardRole.PROJECT_MEMBER
        );
    return boardMemberList.stream().map(board ->
        BoardResponse.builder()
            .boardId(board.getBoard().getBoardId().getValue())
            .boardName(board.getBoard().getBoardName())
            .boardKey(board.getBoard().getBoardKey())
            .description(board.getBoard().getDescription())
            .createdAt(board.getBoard().getCreatedAt())
            .createdByUserId(board.getBoard().getCreatedBy().getUserId().getValue())
            .createdByUserName(board.getBoard().getCreatedBy().getUserName())
            .build()
    ).toList();


  }
  @Override
  public List<BoardResponse> searchBoards(String query) {

    List<Board> boards = boardRepository.searchBoards(query);

    return boards.stream()
        .map(board -> BoardResponse.builder()
            .boardId(board.getBoardId().getValue())
            .boardName(board.getBoardName())
            .boardKey(board.getBoardKey())
            .description(board.getDescription())
            .createdAt(board.getCreatedAt())
            .createdByUserId(board.getCreatedBy().getUserId().getValue())
            .createdByUserName(board.getCreatedBy().getUserName())
            .build()
        )
        .toList();
  }


}
