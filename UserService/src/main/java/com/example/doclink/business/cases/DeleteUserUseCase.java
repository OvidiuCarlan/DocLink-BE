package com.example.doclink.business.cases;

public interface DeleteUserUseCase {
    void initiateUserDeletion(Long userId);
    void completeUserDeletion(Long userId);
}
