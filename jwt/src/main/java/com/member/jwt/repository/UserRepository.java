package com.member.jwt.repository;

import com.member.jwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsername(String username);

    // username을 받아 DB 테이블에서 회원 조회하는 메서드 작성
    UserEntity findByUsername(String username);
}
