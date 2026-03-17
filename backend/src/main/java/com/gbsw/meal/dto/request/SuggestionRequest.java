package com.gbsw.meal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class SuggestionRequest {

    @NotBlank
    private String title;

    private String description;

    private LocalDate deadline;
}
