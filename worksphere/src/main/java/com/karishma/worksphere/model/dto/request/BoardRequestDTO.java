package com.karishma.worksphere.model.dto.request;

import lombok.Data;

@Data
public class BoardRequestDTO {
    private String board_name;
    private String board_key;
    private String description;
    private String justification;
}
