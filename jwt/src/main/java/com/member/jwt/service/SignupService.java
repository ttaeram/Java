package com.member.jwt.service;

import com.member.jwt.dto.SignupRequestDto;
import com.member.jwt.entity.MemberEntity;
import com.member.jwt.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SignupService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignupService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Integer signupProcess(SignupRequestDto signupRequestDto, MultipartFile profileImage) {
        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();
        String nickname = signupRequestDto.getNickname();

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(email)) {

            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // 프로필 이미지 저장
        String profileUrl = saveProfileImage(profileImage);

        // MemberEntity 생성
        MemberEntity memberEntity = MemberEntity.of(signupRequestDto, encodedPassword, profileUrl);

        // DB에 저장
        MemberEntity savedMember = memberRepository.save(memberEntity);

        // 저장된 member_id 반환
        return savedMember.getMemberId();
    }

    private String saveProfileImage(MultipartFile profileImage) {

        if (profileImage.isEmpty()) {

            return "";
        }

        try {
            // 파일 저장 경로 설정
            String directory = System.getProperty("user.home") + "/images/";
            String fileName = profileImage.getOriginalFilename();
            String filePath = directory + System.currentTimeMillis() + "_" + fileName;

            // 디렉토리 생성
            Path path = Paths.get(directory);
            if (!Files.exists(path)) {

                Files.createDirectories(path);
            }

            // 파일 저장
            File destinationPath = new File(filePath);
            profileImage.transferTo(destinationPath);

            return filePath;
        } catch (IOException e) {

            throw new RuntimeException("파일 저장 중 오류 발생!!!!", e);
        }
    }
}
