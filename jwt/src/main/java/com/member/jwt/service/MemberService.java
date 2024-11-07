package com.member.jwt.service;

import com.member.jwt.dto.MemberInfoDto;
import com.member.jwt.entity.MemberEntity;
import com.member.jwt.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {

        this.memberRepository = memberRepository;
    }

    // 회원 조회
    @Transactional
    public MemberInfoDto getMemberInfo(Integer memberId) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

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
    public MemberInfoDto updateMemberInfo(Integer memberId, MemberInfoDto memberInfoDto) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        memberEntity.setNickname(memberInfoDto.getNickname());
        memberEntity.setProfileUrl(memberInfoDto.getProfileUrl());

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

        if (!memberRepository.existsById(memberId)) {

            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        memberRepository.deleteById(memberId);
    }
}
