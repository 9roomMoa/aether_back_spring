package com.groommoa.aether_back_spring.domain.user.repository;

import com.groommoa.aether_back_spring.domain.user.entity.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Member, String> {

    Optional<Member> findByEmail(String email);
}
