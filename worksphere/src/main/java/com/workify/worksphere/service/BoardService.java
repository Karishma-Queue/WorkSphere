package com.workify.worksphere.service;

import com.workify.worksphere.model.dto.response.BoardResponse;
import java.util.List;

public interface BoardService {

  List<BoardResponse> getAllBoards(String userId);

  List<BoardResponse> getMemberBoards(String userId);

  List<BoardResponse> searchBoards(String query);

}
