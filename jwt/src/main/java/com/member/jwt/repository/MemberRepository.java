package com.member.jwt.repository;

import com.member.jwt.entity.MemberEntity;
import com.member.jwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    Boolean existsByEmail(String email);

    // username을 받아 DB 테이블에서 회원 조회하는 메서드 작성
    Optional<MemberEntity> findByEmail(String email);
}
