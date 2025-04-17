package com.example.postservice.postservice.business.dto;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.NotFound;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    @NonNull
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private String category;
}
