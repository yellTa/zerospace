package com.zerospace.zerospace.service;

import com.zerospace.zerospace.domain.Member;
import com.zerospace.zerospace.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl {
    private final MemberRepository memberRepository;


    public boolean hasMember(String email) {
        Member foundMember = memberRepository.findMemberByEmail(email);
        if(foundMember ==null){
            return false;
        }else{
            return true;
        }
    }

    @Transactional
    public void join(String email, String nickName, String userId) {
        Member member = new Member(userId, email, nickName);
        memberRepository.save(member);

    }


    public String getMemberuserId(String email) {
        Member foundMember = memberRepository.findMemberByEmail(email);

        return foundMember.getUserId();
    }
    public String getMemberEmailfromUserId(String userId){
        Member foundMember = memberRepository.findMemberByuserId(userId);

        return foundMember.getEmail();
    }

}
