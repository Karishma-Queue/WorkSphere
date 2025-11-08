package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.*;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.dto.request.BoardRequestUpdateDTO;
import com.karishma.worksphere.model.dto.request.RejectRequestDTO;
import com.karishma.worksphere.model.dto.response.BoardDetailsDTO;
import com.karishma.worksphere.model.dto.response.BoardRequestResponse;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.model.enums.BoardRole;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.model.enums.Status;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardMemberRepository;
import com.karishma.worksphere.repository.BoardRepository;
import com.karishma.worksphere.repository.BoardRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardRequestServiceTest {

    @InjectMocks
    private BoardRequestService service;

    @Mock
    private AuthRepository authRepository;
    @Mock
    private BoardRequestRepository boardRequestRepository;
    @Mock
    private BoardMemberRepository boardMemberRepository;
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private User memberUser;
    private User adminUser;
    private Auth memberAuth;
    private Auth adminAuth;
    private BoardReques
