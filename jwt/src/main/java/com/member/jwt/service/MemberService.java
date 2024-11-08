package com.member.jwt.service;

import com.member.jwt.dto.MemberInfoDto;
import com.member.jwt.dto.MemberUpdateDto;
import com.member.jwt.entity.MemberEntity;
import com.member.jwt.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    String directory = System.getProperty("user.home") + "/images/";

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 회원 조회
    @Transactional
    public MemberInfoDto getMemberInfo(Integer memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        return new MemberInfoDto(
                memberEntity.getMemberId(),
                memberEntity.getEmail(),
                memberEntity.getNickname(),
                memberEntity.getProfileUrl(),
                memberEntity.getRole()
        );
    }

    // 회원 수정
    @Transactional
    public MemberInfoDto updateMemberInfo(Integer memberId, MemberUpdateDto memberUpdateDto) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 수정
        if (memberUpdateDto.getPassword() != null) {

            String encodedPassword = bCryptPasswordEncoder.encode(memberUpdateDto.getPassword());
            memberEntity.setPassword(encodedPassword);
        }

        // 닉네임 수정
        if (memberUpdateDto.getNickname() != null) {

            memberEntity.setNickname(memberUpdateDto.getNickname());
        }

        // 프로필 이미지 수정
        if (memberUpdateDto.getProfileImage() != null && !memberUpdateDto.getProfileImage().isEmpty()) {

            handleProfileImageUpdate(memberEntity, memberUpdateDto.getProfileImage());
        }

        memberRepository.save(memberEntity);

        return new MemberInfoDto(
                memberEntity.getMemberId(),
                memberEntity.getEmail(),
                memberEntity.getNickname(),
                memberEntity.getProfileUrl(),
                memberEntity.getRole()
        );
    }

    // 회원 삭제
    @Transactional
    public void deleteMemberInfo(Integer memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 프로필 사진 삭제
        if (memberEntity.getProfileUrl() != null) {

            File profileImageFile = new File(directory + memberEntity.getProfileUrl());
            if (profileImageFile.exists()) {

                profileImageFile.delete();
            }
        }

        memberRepository.deleteById(memberId);
    }

    private void handleProfileImageUpdate(MemberEntity memberEntity, MultipartFile newProfileImage) {

        try {

            // 기존 파일 삭제
            if (memberEntity.getProfileUrl() != null) {

                File oldFile = new File(directory + memberEntity.getProfileUrl());
                if (!oldFile.exists()) {

                    oldFile.delete();
                }
            }

            // 새로운 파일 저장
            String newFileName = System.currentTimeMillis() + "_" + newProfileImage.getOriginalFilename();
            Path newFilePath = Path.of(directory + newFileName);

            Files.copy(newProfileImage.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            // 엔티티에 새로운 파일 경로 저장
            memberEntity.setProfileUrl(newFileName);
        } catch (IOException e) {

            throw new RuntimeException("프로필 이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }
}
