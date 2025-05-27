package com.example.postservice.postservice.messaging;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletionMessage implements Serializable {
    private Long userId;
    private String userEmail;
    private String action; // DELETION_REQUESTED, DELETION_COMPLETED, DELETION_FAILED
    private String serviceName;
    private String message;
}