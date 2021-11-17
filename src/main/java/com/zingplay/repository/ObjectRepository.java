package com.zingplay.repository;

import com.zingplay.models.Object;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ObjectRepository extends MongoRepository<Object, String> {
  Page<Object> findByIdObjectContainingAndGameAndCountry(String search, String game, String country, Pageable pageable);
  Page<Object> findByIdObjectContaining(String search, Pageable pageable);
  Page<Object> findByIdAndGameAndCountry(String search, String game, String country, Pageable pageable);
  Page<Object> findById(String search, Pageable pageable);
  Page<Object> findAllByGameAndCountry(String game, String country, Pageable pageable);
  List<Object> findByIdObjectIn(List<String> idObjects);
  Optional<Object> findFirstByIdObject(String idObject);
  Optional<Object> findFirstByIdObjectAndGameAndCountry(String idObject,String game, String country);
  boolean existsObjectByIdObjectAndGameAndCountry(String idObject, String game, String country);
  boolean existsObjectByIdObject(String idObject);
  boolean existsObjectByIdObjectIn(List<String> idObject);
  void deleteAllByGameAndCountry(String game, String country);
  void deleteAll();

  Stream<Object> streamAllByGameAndCountry(String game, String country);
}
