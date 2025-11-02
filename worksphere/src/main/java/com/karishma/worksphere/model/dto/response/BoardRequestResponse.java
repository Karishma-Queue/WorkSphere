package com.karishma.worksphere.model.dto.response;

import com.karishma.worksphere.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class BoardRequestResponse {
        private UUID id;
        private String name;
        private User requesterName;

}
