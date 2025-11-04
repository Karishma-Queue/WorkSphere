package com.karishma.worksphere.model.dto.response;

import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BoardDetailsDTO {
    private UUID board_request_id;
    private String board_request_name;
    private String board_request_key;
    private String description;
    private String justification;
    private LocalDateTime requestedAt;
    private User requester;
    private User reviewedBy;
    private LocalDateTime rejectedAt;
    private LocalDateTime approvedAt;
    private String rejection_reason;
    private Status status;

}
