package com.example.postservice.postservice.persistance;

import com.example.postservice.postservice.persistance.entity.PostEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface PostRepository extends MongoRepository<PostEntity, String> {
    List<PostEntity> findByUserId(String userId);
}
