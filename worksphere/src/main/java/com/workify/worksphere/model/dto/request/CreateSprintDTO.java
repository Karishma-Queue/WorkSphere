package com.workify.worksphere.model.dto.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateSprintDTO {
 private String name;
 private String goal;
 private LocalDate startDate;
 private LocalDate endDate;
}
