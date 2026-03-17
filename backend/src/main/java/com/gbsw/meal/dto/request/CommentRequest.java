package com.gbsw.meal.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentRequest {

    @NotBlank
    private String content;

    @Min(1) @Max(5)
    private int rating;
}
