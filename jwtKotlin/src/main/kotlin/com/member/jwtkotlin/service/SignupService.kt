package com.member.jwtkotlin.service

import com.member.jwtkotlin.dto.SignupRequestDto
import com.member.jwtkotlin.entity.MemberEntity
import com.member.jwtkotlin.repository.MemberRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@Service
class SignupService(
    private val memberRepository: MemberRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    fun signupProcess(signupRequestDto: SignupRequestDto, profileImage: MultipartFile): Int? {
        val email = signupRequestDto.email
        val password = signupRequestDto.password
        val nickname = signupRequestDto.nickname

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(email)!!) {
            throw DataIntegrityViolationException("이미 사용 중인 이메일입니다.")
        }

        // 비밀번호 암호화
        val encodedPassword = bCryptPasswordEncoder.encode(password)

        // 프로필 이미지 저장
        val profileUrl = saveProfileImage(profileImage)

        // MemberEntity 생성
        val memberEntity = MemberEntity.of(signupRequestDto, encodedPassword, profileUrl)

        // DB에 저장
        val savedMember = memberRepository.save(memberEntity)

        // 저장된 member_id 반환
        return savedMember.memberId
    }

    private fun saveProfileImage(profileImage: MultipartFile): String {
        if (profileImage.isEmpty) {
            return ""
        }

        try {
            // 파일 저장 경로 설정
            val directory = System.getProperty("user.home") + "/images/"
            val fileName = profileImage.originalFilename
            val filePath = directory + System.currentTimeMillis() + "_" + fileName

            // 디렉토리 생성
            val path = Paths.get(directory)
            if (!Files.exists(path)) {
                Files.createDirectories(path)
            }

            // 파일 저장
            val destinationPath = File(filePath)
            profileImage.transferTo(destinationPath)

            return filePath
        } catch (e: IOException) {
            throw RuntimeException("파일 저장 중 오류 발생!!!!", e)
        }
    }
}