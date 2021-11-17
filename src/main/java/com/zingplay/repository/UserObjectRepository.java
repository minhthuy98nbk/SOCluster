package com.zingplay.repository;

import com.zingplay.models.Object;
import com.zingplay.models.User;
import com.zingplay.models.UserObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserObjectRepository extends MongoRepository<UserObject, String> {
    Page<UserObject> findAllByUser(User user, Pageable pageable);
    Page<UserObject> findAllByUserAndObjectIn(User user,List<Object> object, Pageable pageable);

    public void deleteAllByObject(Object object);
    public void deleteAllByUser(User user);
    Stream<UserObject> streamAllByObject(Object object);
    Stream<UserObject> streamAllByUser(User user);
    Page<UserObject> findByObject_Id(String idObject, Pageable pageable);
    Optional<UserObject> findFirstByUserAndObject(User user, Object object);
    Page<UserObject> findByUserIsInAndObject_Id(List<User> users, String object_id, Pageable pageable);

}
