package com.member.jwt.service

import com.member.jwt.dto.MemberInfoDto
import com.member.jwt.dto.MemberUpdateDto
import com.member.jwt.entity.MemberEntity
import com.member.jwt.repository.MemberRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    private val directory: String = System.getProperty("user.home") + "/images/"

    // 회원 조회
    @Transactional
    fun getMemberInfo(memberId: Int): MemberInfoDto {
        val memberEntity = memberRepository.findById(memberId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        return MemberInfoDto(
            memberId = memberEntity?.memberId,
            email = memberEntity?.email,
            nickname = memberEntity?.nickname,
            profileUrl = memberEntity?.profileUrl,
            role = memberEntity?.role
        )
    }

    // 회원 수정
    @Transactional
    fun updateMemberInfo(memberId: Int, memberUpdateDto: MemberUpdateDto): MemberInfoDto {
        val memberEntity = memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        memberUpdateDto.password?.let {
            val encodedPassword = bCryptPasswordEncoder.encode(it)
            memberEntity?.password = encodedPassword
        }

        memberUpdateDto.nickname?.let {
            memberEntity?.nickname = it
        }

        memberUpdateDto.profileImage?.takeIf { !it.isEmpty }?.let {
            if (memberEntity != null) {
                handleProfileImageUpdate(memberEntity, it)
            }
        }

        if (memberEntity != null) {
            memberRepository.save(memberEntity)
        }

        return MemberInfoDto(
            memberId = memberEntity?.memberId,
            email = memberEntity?.email,
            nickname = memberEntity?.nickname,
            profileUrl = memberEntity?.profileUrl,
            role = memberEntity?.role
        )
    }

    // 회원 삭제
    @Transactional
    fun deleteMemberInfo(memberId: Int) {
        val memberEntity = memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        memberEntity?.profileUrl?.let { profileUrl ->
            val profileImageFile = File(directory + profileUrl)
            if (profileImageFile.exists()) {
                profileImageFile.delete()
            }
        }

        memberRepository.deleteById(memberId)
    }

    private fun handleProfileImageUpdate(memberEntity: MemberEntity, newProfileImage: MultipartFile) {
        try {
            memberEntity.profileUrl?.let { oldProfileUrl ->
                val oldFile = File(directory + oldProfileUrl)
                if (oldFile.exists()) {
                    oldFile.delete()
                }
            }

            val newFileName = "${System.currentTimeMillis()}_${newProfileImage.originalFilename}"
            val newFilePath = Path.of(directory + newFileName)

            Files.copy(newProfileImage.inputStream, newFilePath, StandardCopyOption.REPLACE_EXISTING)

            memberEntity.profileUrl = newFileName
        } catch (e: IOException) {
            throw RuntimeException("프로필 이미지 업로드 중 오류가 발생했습니다.", e)
        }
    }
}
