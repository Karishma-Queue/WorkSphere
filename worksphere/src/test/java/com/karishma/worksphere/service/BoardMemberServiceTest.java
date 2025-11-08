package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.NotFoundException;
import com.karishma.worksphere.exception.RequestAlreadyProcessedException;
import com.karishma.worksphere.model.dto.request.AddBoardMemberDTO;
import com.karishma.worksphere.model.dto.response.AddBoardMemberResponseDTO;
import com.karishma.worksphere.model.dto.response.AllBoardMemberDTO;
import com.karishma.worksphere.model.dto.response.BoardMemberDetailsDTO;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.model.enums.BoardRole;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardMemberRepository;
import com.karishma.worksphere.repository.BoardRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardMemberServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardMemberRepository boardMemberRepository;

    @InjectMocks
    private BoardMemberService boardMemberService;

    private User user;
    private Auth auth;
    private Board board;
    private BoardMember boardMember;
    private AddBoardMemberDTO addBoardMemberDTO;
    private UUID boardId;
    private UUID boardMemberId;

    @BeforeEach
    void setUp() {
        boardId = UUID.randomUUID();
        boardMemberId = UUID.randomUUID();

        user = User.builder()
                .user_id(UUID.randomUUID())
                .user_name("John Doe")
                .role(Role.MEMBER)
                .job_title("Developer")
                .department("Engineering")
                .profile_picture_url("profile.png")
                .build();

        auth = Auth.builder()
                .auth_id(UUID.randomUUID())
                .email("john@example.com")
                .hashed_pass("hashed")
                .user(user)
                .build();

        board = Board.builder()
                .board_id(boardId)
                .board_name("Project Alpha")
                .board_key("PA")
                .build();

        boardMember = BoardMember.builder()
                .board_member_id(boardMemberId)
                .board(board)
                .user(user)
                .boardRole(BoardRole.PROJECT_MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        addBoardMemberDTO = AddBoardMemberDTO.builder()
                .email("john@example.com")
                .build();
    }

    // ✅ TEST 1: Add Board Member - Success
    @Test
    void testAddBoardMember_Success() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(auth));
        when(boardMemberRepository.existsByBoardAndUser(board, user)).thenReturn(false);
        when(boardMemberRepository.save(any(BoardMember.class))).thenAnswer(inv -> {
            BoardMember bm = inv.getArgument(0);
            bm.setBoard_member_id(UUID.randomUUID());
            return bm;
        });

        AddBoardMemberResponseDTO response = boardMemberService.addBoardMember(boardId, addBoardMemberDTO);

        assertNotNull(response);
        assertEquals(board.getBoard_name(), response.getBoard().getBoard_name());
        assertEquals(user.getUser_name(), response.getUser().getUser_name());
        verify(boardMemberRepository, times(1)).save(any(BoardMember.class));
    }

    // ❌ TEST 2: Add Board Member - Already Exists
    @Test
    void testAddBoardMember_AlreadyExists() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(auth));
        when(boardMemberRepository.existsByBoardAndUser(board, user)).thenReturn(true);

        assertThrows(RequestAlreadyProcessedException.class,
                () -> boardMemberService.addBoardMember(boardId, addBoardMemberDTO));
    }

    // ❌ TEST 3: Add Board Member - User Not Found
    @Test
    void testAddBoardMember_UserNotFound() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> boardMemberService.addBoardMember(boardId, addBoardMemberDTO));
    }

    // ❌ TEST 4: Remove Board Member - Success
    @Test
    void testRemoveBoardMember_Success() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(boardMemberRepository.findById(boardMemberId)).thenReturn(Optional.of(boardMember));

        ResponseEntity<String> response = boardMemberService.removeBoardMember(boardId, boardMemberId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Member deleted successfully", response.getBody());
        verify(boardMemberRepository, times(1)).delete(boardMember);
    }

    // ❌ TEST 5: Remove Board Member - Board Not Found
    @Test
    void testRemoveBoardMember_BoardNotFound() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> boardMemberService.removeBoardMember(boardId, boardMemberId));
    }

    // ❌ TEST 6: Get Member Details - Success
    @Test
    void testGetMemberDetails_Success() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(boardMemberRepository.findById(boardMemberId)).thenReturn(Optional.of(boardMember));
        when(authRepository.findByUser(user)).thenReturn(Optional.of(auth));

        BoardMemberDetailsDTO dto = boardMemberService.getMemberDetails(boardId, boardMemberId);

        assertEquals(board.getBoard_id(), dto.getBoard_id());
        assertEquals(user.getUser_name(), dto.getUser_name());
        assertEquals("john@example.com", dto.getEmail());
    }

    // ❌ TEST 7: Get Member Details - Member Not in Board
    @Test
    void testGetMemberDetails_NotInBoard() {
        Board otherBoard = Board.builder().board_id(UUID.randomUUID()).board_name("Another").build();
        boardMember.setBoard(otherBoard);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(boardMemberRepository.findById(boardMemberId)).thenReturn(Optional.of(boardMember));

        assertThrows(NotFoundException.class,
                () -> boardMemberService.getMemberDetails(boardId, boardMemberId));
    }

    // ✅ TEST 8: Get Board Members - Success
    @Test
    void testGetBoardMembers_Success() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(boardMemberRepository.findByBoardId(boardId)).thenReturn(List.of(boardMember));

        List<AllBoardMemberDTO> members = boardMemberService.getBoardMembers(boardId);

        assertEquals(1, members.size());
        assertEquals(user.getUser_name(), members.get(0).getUser_name());
    }

    // ❌ TEST 9: Get Board Members - Board Not Found
    @Test
    void testGetBoardMembers_BoardNotFound() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> boardMemberService.getBoardMembers(boardId));
    }
}
