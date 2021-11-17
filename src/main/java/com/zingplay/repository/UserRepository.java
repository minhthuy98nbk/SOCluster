package com.zingplay.repository;

import com.zingplay.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByUserIdIn(List<String> userIds);
    Optional<User> findFirstByUserId(Integer userId);
    Optional<User> findFirstById(String id);
    List<User> findByUserId(Integer search);
    Page<User> findByUserId(Integer search, Pageable pageable);
    Page<User> findAll( Pageable pageable);

    // Stream<User> streamAll();
    Stream<User> streamAllByTimeOnlineBefore(Date lastTimeOnline);
    void deleteAll();
    Page<User> findByTimeOnlineBefore(Date lastTimeOnline, Pageable pageable);
    long countByTimeOnlineBefore(Date lastTimeOnline);

}
