package com.workify.worksphere.model.dto.response;

import com.workify.worksphere.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class BoardRequestResponse {
        private String id;
        private String name;
        private User requesterName;

}
