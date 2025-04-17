package com.example.postservice.postservice.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {
    @Id
    private String id;

    private String userId;
    private String title;
    private String content;
    private String category;
}
