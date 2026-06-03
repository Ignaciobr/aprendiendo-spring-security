package com.app.persistence.entity.repository;

import com.app.persistence.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;


//lo uysamos para que de el metodo save delete yeso
public interface UserRepository  extends CrudRepository <UserEntity, Long > {

        Optional<UserEntity> findUserEntityByUsername(String username);
}
