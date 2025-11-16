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

public interface BoardMemberService {

  List<AllBoardMemberDTO> getBoardMembers(String boardId);
  ResponseEntity<String> removeBoardMember(String boardId, String memberId);
  AddBoardMemberResponseDTO addBoardMember(String boardId, AddBoardMemberDTO request);
}
