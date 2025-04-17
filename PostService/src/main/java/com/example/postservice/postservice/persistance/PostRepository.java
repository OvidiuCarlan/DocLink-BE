package com.example.postservice.postservice.persistance;

import com.example.postservice.postservice.persistance.entity.PostEntity;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface PostRepository extends MongoRepository<PostEntity, String> {

}
