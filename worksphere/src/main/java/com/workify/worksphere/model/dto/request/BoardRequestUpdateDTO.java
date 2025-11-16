package com.workify.worksphere.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequestUpdateDTO {
    private String board_request_name;
    private String board_request_key;
    private String description;
    private String justification;
}
