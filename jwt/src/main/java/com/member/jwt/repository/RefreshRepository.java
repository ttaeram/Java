package com.member.jwt.repository;

import com.member.jwt.entity.RefreshEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);

    // 이메일로 리프레시 토큰을 조회
    Optional<RefreshEntity> findByEmail(String email);

    // 이메일로 리프레시 토큰을 삭제
    @Transactional
    void deleteByEmail(String email);
}
