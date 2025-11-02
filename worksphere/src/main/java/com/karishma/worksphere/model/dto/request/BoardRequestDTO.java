package com.karishma.worksphere.model.dto.request;

import lombok.Data;

@Data
public class BoardRequestDTO {
    private String board_request_name;
    private String board_request_key;
    private String description;
    private String justification;
}
