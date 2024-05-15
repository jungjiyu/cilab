package com.practice.jwt1.repository;

import com.practice.jwt1.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public abstract Boolean existsByUsername(String username);
    public abstract UserEntity findByUsername(String username);
}
