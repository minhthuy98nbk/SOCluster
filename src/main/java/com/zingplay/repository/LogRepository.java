package com.zingplay.repository;

import com.zingplay.models.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LogRepository extends MongoRepository<Log, String> {
    @Override
    List<Log> findAll();

    @Override
    Page<Log> findAll(Pageable pageable);

    Page<Log> findByAndGameAndCountry(String game, String country, Pageable pageable);
    Page<Log> findByUsernameContainingAndGameAndCountry(String search, String game, String country, Pageable pageable);
    Page<Log> findByMessageContainingOrUsernameContainingAndGameAndCountry(String msg, String username, String game, String country, Pageable pageable);

    void deleteAllByGameAndCountry(String game, String country);
}
