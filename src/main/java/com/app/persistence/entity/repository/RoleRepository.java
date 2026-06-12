package com.app.persistence.entity.repository;

import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.RoleEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    List<RoleEntity> findByRoleEnumIn(List<RoleEnum> roleEnums);

}