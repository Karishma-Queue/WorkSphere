package com.workify.worksphere.service;

import com.workify.worksphere.model.dto.response.BoardResponse;
import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.entity.BoardMember;
import com.workify.worksphere.model.enums.BoardRole;
import com.workify.worksphere.repository.BoardMemberRepository;
import com.workify.worksphere.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    public List<BoardResponse> getAllBoards(String user_id)
    {
        List<Board> boardList=boardRepository.findByCreatedBy_UserId(user_id);
        return boardList.stream().map(board ->
                BoardResponse.builder()
                        .boardId(board.getBoardId())
                        .boardName(board.getBoardName())
                        .boardKey(board.getBoardKey())
                        .description(board.getDescription())
                        .createdAt(board.getCreatedAt())
                        .createdByUserId(board.getCreatedBy().getUserId())
                        .createdByUserName(board.getCreatedBy().getUserName())
                        .build()
        ).toList();


    }
    public List<BoardResponse> getMemberBoards( String user_id)
    {
        List<BoardMember> boardMemberList =
                boardMemberRepository.findByUser_UserIdAndBoardRole(
                        user_id,
                        BoardRole.PROJECT_MEMBER
                );
        return boardMemberList.stream().map(board ->
                BoardResponse.builder()
                        .boardId(board.getBoard().getBoardId())
                        .boardName(board.getBoard().getBoardName())
                        .boardKey(board.getBoard().getBoardKey())
                        .description(board.getBoard().getDescription())
                        .createdAt(board.getBoard().getCreatedAt())
                        .createdByUserId(board.getBoard().getCreatedBy().getUserId())
                        .createdByUserName(board.getBoard().getCreatedBy().getUserName())
                        .build()
        ).toList();


    }
    public List<BoardResponse> searchBoards(String query) {

        List<Board> boards = boardRepository.searchBoards(query);

        return boards.stream()
                .map(board -> BoardResponse.builder()
                        .boardId(board.getBoardId())
                        .boardName(board.getBoardName())
                        .boardKey(board.getBoardKey())
                        .description(board.getDescription())
                        .createdAt(board.getCreatedAt())
                        .createdByUserId(board.getCreatedBy().getUserId())
                        .createdByUserName(board.getCreatedBy().getUserName())
                        .build()
                )
                .toList();
    }


}
